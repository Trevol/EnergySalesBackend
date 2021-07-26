import database_creation.utils.println
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.test.Test

class LastDayOfMonthTest {
    @Test
    fun lastDayOfMonth() {
        Assert.assertEquals(LocalDate.of(2020, 3, 1).minusDays(1), LocalDate.of(2020, 2, 29))
        Assert.assertEquals(LocalDate.of(2020, 2, 1).with(TemporalAdjusters.lastDayOfMonth()), LocalDate.of(2020, 2, 29))
    }
}