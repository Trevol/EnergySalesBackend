import database_creation.utils.println
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class CyclicalSerializationTest {
    @Test
    fun DoTest() {
        val restoredOrganization = Json.decodeFromString<Organization?>("null")
        restoredOrganization?.name.println()
    }
}

@Serializable
class Organization {
    var name: String = ""
}