package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ImportViewModel: ViewModel() {
    val openAI = OpenAI(
        token = "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",
        timeout = Timeout(socket = 60.seconds),
    )

    fun import(imagePath: PlatformFile) {
        println("Importing image from ${imagePath.path}")
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = "You are a helpful assistant speaking in JSON language!"
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = "Hello!"
                )
            ),
            maxTokens = 100,
            responseFormat = ChatResponseFormat.JsonObject
        )
        val completions: Flow<ChatCompletionChunk> = openAI.chatCompletions(chatCompletionRequest)

        viewModelScope.launch {
            completions.collect { chunk ->
                chunk.choices.forEach {
                    if (it.finishReason == null && it.delta != null && it.delta?.content != null) {
                        print(it.delta?.content)
                    }
                }
            }
        }
    }
}