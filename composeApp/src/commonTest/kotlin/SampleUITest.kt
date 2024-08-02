import androidx.compose.material.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SampleUITest {

    @Test
    fun sampleUITest() = runComposeUiTest {
        setContent {
            Text("Hello, World!")
        }

        onNodeWithText("Hello, World!").assertExists()
    }
}