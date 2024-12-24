@file:OptIn(ExperimentalUuidApi::class)

package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import model.*
import repository.JubilareRepository
import java.io.File
import java.net.URLConnection
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ImportViewModel(
    private val repository: JubilareRepository
): ViewModel() {

    private val _file = MutableStateFlow<File?>(null)
    val file: StateFlow<File?> get() = _file
    private val _jubilareState = MutableStateFlow<List<Jubilar>>(emptyList())
    val jubilareState: StateFlow<List<Jubilar>> get() = _jubilareState


    val openAI = OpenAI(
        token = "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",
        timeout = Timeout(socket = 60.seconds),
    )

    fun import(imagePath: PlatformFile) {
        println("Importing image from ${imagePath.path}")
        val file = File(imagePath.file.name + ".json")
        viewModelScope.launch {
            if (!file.exists()) {
                val chatCompletionRequest = ChatCompletionRequest(
                    model = ModelId("gpt-4o-mini"),
                    messages = listOf(
                        ChatMessage(
                            role = ChatRole.System,
                            content = "You are a data extraction specialist. " +
                                    "I need the data from the pictures in a structured JSON Form. " +
                                    "The image contains a table. I need a JSON object for every row. " +
                                    "The root element shall be a JSON array with the keyname jubilare. " +
                                    "The keys of the JSON object shall be based on the column header and the following mapping from header to JSON Key: " +
                                    "Familienname, Zusatz, Titel -> lastname; Vorname -> firstname; Geb.-Datum -> originalJubilarDate; Straße und Hausnummer -> address; G -> gender; ignore alter, kf, fs, STIF columns"
                        ),
                        ChatMessage(
                            role = ChatRole.User,
                            messageContent = ListContent(
                                content = listOf(
                                    ImagePart(
                                        url = "data:${getMimeType(imagePath)};base64," + Base64.getEncoder()
                                            .encodeToString(imagePath.readBytes())
                                    ),
                                    TextPart(
                                        text = "Extract the date from the image"
                                    )
                                )
                            ),
                        )
                    ),
                    //maxTokens = 200,
                    responseFormat = ChatResponseFormat.JsonObject
                )

                val completions: Flow<ChatCompletionChunk> = openAI.chatCompletions(chatCompletionRequest)
                val result = StringBuilder()

                completions.collect { chunk ->
                    chunk.choices.forEach {
                        if (it.finishReason == null && it.delta != null && it.delta?.content != null) {
                            result.append(it.delta?.content)
                            print(it.delta?.content)
                        } else if (it.finishReason != null) {
                            println()
                            println(it.finishReason)
                        }
                    }
                }

                try {
                    val jsonResponse = result.toString()
                    file.writeText(jsonResponse)
                    println("Wrote JSON to ${file.absolutePath}")
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("Failed to write JSON: " + e.message)
                }
            }

            val jubilareList: JubilareWrapper = Json.decodeFromString(file.readText())
            _jubilareState.update { jubilareList.jubilare }
            _file.update { imagePath.file }
        }
    }

    fun updateJubilar(index: Int, updatedJubilar: Jubilar) {
        _jubilareState.value = _jubilareState.value.toMutableList().apply {
            this[index] = updatedJubilar
        }
    }

    fun insertEmptyJubilar(index: Int) {
        _jubilareState.value = _jubilareState.value.toMutableList().apply {
            val defaultJubilar = BirthdayJubilar(
                lastName = "",
                firstName = "",
                address = "",
                originalJubilarDate = LocalDate(1900, 1, 1), // Default date
                gender = Gender.OTHER, // Default gender
                optOut = false,
                comment = ""
            )
            add(index.coerceIn(0, size), defaultJubilar) // Safely insert at a valid index
        }
    }

    fun delete(currentIndex: Int) {
        _jubilareState.value = _jubilareState.value.toMutableList().apply {
            removeAt(currentIndex)
        }
    }

    fun saveJubilar(currentIndex: Int) {
        val jubilar = _jubilareState.value[currentIndex]
        repository.insert(jubilar)
    }

    fun isJubilarStored(index: Int): Flow<Boolean> {
        val jubilarList = jubilareState.value
        if (index < 0 || index >= jubilarList.size) return flowOf(false)
        return repository.exists(jubilarList[index].jubilarId)
    }

}

fun getMimeType(file: PlatformFile): String? {
    return URLConnection.guessContentTypeFromName(file.name)
}