import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class SampleTest {

    @Test
    fun sampleTests() {
        assertThat(2 + 2).isEqualTo(4)
    }
}