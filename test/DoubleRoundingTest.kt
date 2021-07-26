import com.tavrida.utils.round2
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DoubleRoundingTest {
    @Test
    fun round0() {
        (1.01 + 1.02).round2() shouldBe 2.03
    }
}