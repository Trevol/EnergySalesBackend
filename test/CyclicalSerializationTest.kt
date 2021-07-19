import database_creation.utils.println
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class CyclicalSerializationTest {
    @Test
    fun DoTest() {
        val organization = Organization().apply {
            val org = this
            name = "Org 123"
            counters = mutableListOf(
                OrgCounter().apply {
                    sn="123"
                    organization = org
                },
                OrgCounter().apply {
                    sn="456"
                    organization=org
                }
            )
        }

        val json = Json.encodeToString(organization)

        json.println()

        val restoredOrganization = Json.decodeFromString<Organization>(json)
        restoredOrganization.name.println()
    }
}

@Serializable
class Organization {
    lateinit var name: String
    var counters = mutableListOf<OrgCounter>()
}

@Serializable
class OrgCounter() {
    lateinit var sn: String
    lateinit var organization: Organization
}