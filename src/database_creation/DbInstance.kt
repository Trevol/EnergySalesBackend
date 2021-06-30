import com.tavrida.energysales.data_access.dbmodel.tables.allTables
import database_creation.utils.deleteFileInDir
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

class DbInstance(
    val dbDir: String = "./databases/",
    val dbName: String = "ENERGY_SALES_MOBILE"
) {
    fun get(recreate: Boolean): Database {
        if (recreate) {
            deleteFileInDir(dbDir, "$dbName.")
            schemaCreated = false
        }
        return connect().ensureSchema()
    }

    private fun connect() = "jdbc:h2:${File(dbDir, dbName)}"
        .let { url ->
            Database.connect(url)
        }

    companion object {
        private var schemaCreated = false
        private fun Database.ensureSchema(): Database {
            if (schemaCreated) return this
            transaction(this) {
                SchemaUtils.create(*allTables)
            }
            schemaCreated = true
            return this
        }
    }
}