package database_creation.xlsx

import com.tavrida.energysales.data_access.dbmodel.tables.OrganizationStructureUnits
import com.tavrida.energysales.data_access.models.DataContext
import com.tavrida.energysales.data_access.models.Organization
import com.tavrida.energysales.data_access.models.OrganizationStructureUnit
import database_creation.utils.dataContextWithTimestampedDb
import database_creation.xlsx.reader.OrganizationsWithStructureXlsReader
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import com.tavrida.energysales.data_access.models.transaction
import java.io.File
import java.time.LocalDateTime
import java.time.Month

typealias TimeAndFile = Pair<LocalDateTime, File>

fun main() {
    val baseDir = File("./databases/xlsx/import_all")
    val config = Importer.ImportConfig(
        orgStructureFile = File(baseDir, "org_units.xlsx"),
        timeToReadings = listOf(
            LocalDateTime.of(2021, Month.JULY, 1, 11, 30, 33) to File(baseDir, "import 21.06.xlsx"),
            // LocalDateTime.of(2021, Month.JUNE, 1, 12, 22, 15) to File(baseDir, "import 21.05.xlsx")
        )
    )

    val dc = dataContextWithTimestampedDb(databasesDir = "./databases", dbNameSuffix = "xls_ALL")
    Importer(config).xlsToOrganizationsAndStructureToDb(dc)
}


private class Importer(val config: ImportConfig) {
    data class ImportConfig(
        val orgStructureFile: File,
        val timeToReadings: List<TimeAndFile>
    )

    fun xlsToOrganizationsAndStructureToDb(dataContext: DataContext) {
        val organizationsAndStructure = xlsToOrganizationsAndStructure()
        dataContext.save(organizationsAndStructure)
    }

    private fun DataContext.save(organizationsAndStructure: OrganizationsAndStructure) {
        transaction(this) {
            this@save.saveOrgStructure(organizationsAndStructure.structure)
            this@save.insertAll(organizationsAndStructure.organizations)
        }
    }

    data class OrganizationsAndStructure(
        val organizations: List<Organization>,
        val structure: List<OrganizationStructureUnit>
    )

    fun xlsToOrganizationsAndStructure(): OrganizationsAndStructure {
        val orgStructureUnits = OrganizationsWithStructureXlsReader.readOrgStructureUnits(
            config.orgStructureFile,
            firstRowContainsHeader = true
        )

        for ((time, readingsFile) in config.timeToReadings) {
            val organizations = OrganizationsWithStructureXlsReader.readOrganizations(
                readingsFile,
                firstRowContainsHeader = true
            )
            // convert list<OrganizationRecord> to list<Organization>
        }
        TODO()
    }

    private fun DataContext.saveOrgStructure(orgStructureUnits: List<OrganizationStructureUnit>) {
        transaction(this.db) {
            orgStructureUnits.forEach { u ->
                OrganizationStructureUnits.insert {
                    it[id] = u.id
                    it[name] = u.name
                    it[parentId] = u.parentId
                    it[comment] = u.comment
                }
            }
        }
    }
}

fun TODO_ITEMS() {
    /*
    - подготовить показания по месяцам
        - ассоциировать файл (или worksheet в файле) с временной меткой сбора показаний
    - корпуса
        - таблица + сущность  (плоская или иерархия?)
        - прописать в экселях
    * */
}