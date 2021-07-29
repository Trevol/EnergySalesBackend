import com.tavrida.utils.*
import database_creation.utils.println
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DoubleRoundingTest {
    @Test
    fun round0() {
        (1.01 + 1.02).round2() shouldBe 2.03
    }

    @Test
    fun overloadDoubleOperators() {
        val d: Double? = 1.01
        val other: Double? = 3.0
        (d * other).println()
        (d - other).println()
        (d - null).println()
        (null - d).println()
        (1.0 * d).println()
        (d * 1.0).println()
    }
}