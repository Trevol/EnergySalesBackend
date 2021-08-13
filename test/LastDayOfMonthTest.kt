import database_creation.utils.println
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.time.toDuration
import kotlin.time.toKotlinDuration

class LastDayOfMonthTest {
    @Test
    fun lastDayOfMonth() {
        LocalDate.of(2020, 3, 1).minusDays(1) shouldBe LocalDate.of(2020, 2, 29)
        LocalDate.of(2020, 2, 1).with(TemporalAdjusters.lastDayOfMonth()) shouldBe LocalDate.of(2020, 2, 29)
    }
}