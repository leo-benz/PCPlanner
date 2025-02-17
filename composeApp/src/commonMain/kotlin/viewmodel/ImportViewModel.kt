@file:OptIn(ExperimentalUuidApi::class)

package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import dev.leobenz.pcplanner.OPENAI_API_KEY
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import model.BirthdayJubilar
import model.Gender
import model.Jubilar
import model.JubilareWrapper
import repository.JubilareRepository
import java.io.File
import java.net.URLConnection
import java.security.MessageDigest
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi

class ImportViewModel(
    private val repository: JubilareRepository
): ViewModel() {

    private val _file = MutableStateFlow<File?>(null)
    val file: StateFlow<File?> get() = _file
    private val _jubilareState = MutableStateFlow<List<Jubilar>>(emptyList())
    val jubilareState: StateFlow<List<Jubilar>> get() = _jubilareState
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    private val _loadingMessage = MutableStateFlow("")
    val loadingMessage: StateFlow<String> get() = _loadingMessage

    val openAI = OpenAI(
        token = OPENAI_API_KEY,
        timeout = Timeout(socket = 60.seconds),
    )

    private fun getAppDataDirectory(): File {
        val appName = "PCPlanner"
        val os = System.getProperty("os.name").lowercase()

        return when {
            "win" in os ->
                // %APPDATA%\AppName
                File(System.getenv("APPDATA") ?: System.getProperty("user.home"), appName)
            "mac" in os ->
                // ~/Library/Application Support/AppName
                File(System.getProperty("user.home"), "Library/Application Support/$appName")
            else ->
                // ~/.appname (or use XDG environment vars if you prefer)
                File(System.getProperty("user.home"), ".$appName")
        }
    }

    fun ByteArray.sha256Hex(): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(this)
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun countOccurrences(sb: java.lang.StringBuilder, target: String): Int {
        if (target.isEmpty()) {
            return 0
        }

        val targetLength = target.length
        var count = 0

        // Iterate over the StringBuilder
        var i = 0
        while (i <= sb.length - targetLength) {
            var found = true

            // Check if the substring matches the target
            for (j in 0..<targetLength) {
                if (sb[i + j] != target[j]) {
                    found = false
                    break
                }
            }

            if (found) {
                count++
                i += targetLength - 1 // Skip ahead by the length of the target
            }
            i++
        }

        return count
    }

    fun import(imagePath: PlatformFile) {
        _loadingMessage.value = "Import started"
        viewModelScope.launch {
            println("Importing image from ${imagePath.path}")
            val cacheDir = File(getAppDataDirectory(), "cache/").apply { mkdirs() }

            val fileBytes = imagePath.readBytes()
            val hashValue = fileBytes.sha256Hex()
            val file = File(cacheDir, "$hashValue.json")

            if (!file.exists()) {
                _isLoading.value = true
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

                try {
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

                        _loadingMessage.value = "${countOccurrences(result, "firstname")} Jubilare extrahiert"
                    }

                    val jsonResponse = result.toString()
                    file.writeText(jsonResponse)
                    println("Wrote JSON to ${file.absolutePath}")

                    _loadingMessage.value = "Jubilare zwischengespeichert"
                } catch (e: AuthenticationException) {
                    println("Invalid API Key: ${OPENAI_API_KEY}")
                    _loadingMessage.value = "Ungültiger API Key ${OPENAI_API_KEY}. Bitte Support Kontaktieren."
                } catch (e: Exception) {
                    e.printStackTrace()
                    _loadingMessage.value = e.localizedMessage
                    println("Failed to write fetch or write JSON: " + e.message)
                } finally {
                    _isLoading.value = false
                }
            }

            val jubilareList: JubilareWrapper = Json.decodeFromString(file.readText())

            _jubilareState.update {
                jubilareList.jubilare.map {
                    val storedJubilarFlow = repository.getStoredJubilar(it)
                    val storedJubilar = storedJubilarFlow.first()
                    if (storedJubilar != null) {
                        storedJubilar
                    } else {
                        it
                    }
                }
            }
            _file.update { imagePath.file }
            _loadingMessage.value = "Import abgeschlossen"
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