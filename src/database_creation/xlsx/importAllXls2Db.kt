package database_creation.xlsx

import com.tavrida.energysales.data_access.tables.OrganizationStructureUnits
import com.tavrida.energysales.data_access.models.*
import database_creation.utils.checkIsTrue
import database_creation.utils.checkNotEmpty
import database_creation.xlsx.reader.OrganizationsWithStructureXlsReader
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import database_creation.utils.println
import database_creation.xlsx.OrganizationChecker.check
import database_creation.xlsx.OrganizationChecker.checkSerialNumberDuplicates
import java.io.File
import java.time.LocalDateTime
import java.time.Month

typealias TimeAndFile = Pair<LocalDateTime, File>
typealias OrganizationCounterReadingRecord = OrganizationsWithStructureXlsReader.OrganizationCounterReadingRecord


fun main() {
    val baseDir = File("./databases/xlsx/import_all")
    val config = Importer.ImportConfig(
        orgStructureFile = File(baseDir, "org_units.xlsx"),
        timeToReadings = listOf(
            LocalDateTime.of(2021, Month.JULY, 1, 11, 30, 33) to File(baseDir, "import 21.06.xlsx"),
            LocalDateTime.of(2021, Month.JUNE, 1, 12, 22, 15) to File(baseDir, "import 21.05.xlsx")
        )
    )

    val orgAndStruct = Importer(config).xlsToOrganizationsAndStructure()
    orgAndStruct.structure.size.println()
    orgAndStruct.organizations.size.println()
    orgAndStruct.organizations.flatMap { it.counters }.size.println()

    /*val dc = dataContextWithTimestampedDb(databasesDir = "./databases", dbNameSuffix = "xls_ALL")
    Importer(config).xlsToOrganizationsAndStructureToDb(dc)
    dc.loadAll().run {
        size.println()
        flatMap { it.counters }.size.println()
    }*/
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

        val resultingOrganizations = mutableListOf<Organization>()

        config.timeToReadings
            .sortedByDescending { (time, readingsFile) -> time } // сначала обрабатываются новые файлы (актуальные организации)
            .map { (time, readingsFile) ->
                time to OrganizationsWithStructureXlsReader.readOrganizations(
                    readingsFile,
                    firstRowContainsHeader = true
                ).also { it.checkSerialNumberDuplicates() }
            }
            .forEach { (readingTime, orgCounterReadingRecs) ->
                val currentOrganizations = orgCounterReadingRecs.toOrganizations(orgStructureUnits, readingTime)
                currentOrganizations.check()
                mergeOrganizationsReadings2(resultingOrganizations, currentOrganizations)
            }

        return OrganizationsAndStructure(resultingOrganizations, orgStructureUnits)
    }

    fun mergeOrganizationsReadings(
        resultingOrganizations: MutableList<Organization>,
        currentOrganizations: List<Organization>
    ) {
        val subsequentMerge = resultingOrganizations.isNotEmpty()
        fun String.log() {
            if (subsequentMerge) {
                println()
            }
        }
        "----------------".log()
        for (currentOrg in currentOrganizations) {
            val matchedOrg = resultingOrganizations.firstOrNull { it.name == currentOrg.name }
            if (matchedOrg == null) {
                resultingOrganizations.add(currentOrg)
                "Organization ${currentOrg.name} added".log()
            } else {
                currentOrg.counters.forEach { curCounter ->
                    checkIsTrue(curCounter.readings.size == 1)
                    val counter = matchedOrg.counters.firstOrNull { it.serialNumber == curCounter.serialNumber }
                        ?: TODO("Counter not found. Organization: ${matchedOrg.name}. SerialNumber: ${curCounter.serialNumber}")
                    counter.readings.add(curCounter.readings.first())
                }
            }
        }
        resultingOrganizations.check()
    }

    fun mergeOrganizationsReadings2(
        resultingOrganizations: MutableList<Organization>,
        currentOrganizations: List<Organization>
    ) {
        val subsequentMerge = resultingOrganizations.isNotEmpty()
        fun String.log() {
            if (subsequentMerge) { //
                this.println()
            }
        }

        "----------------".log()

        for (currentOrg in currentOrganizations) {
            // find organization by serialNumber
            // or resultingOrganizations.findMatchingOrganization(currentOrg)
            val matchedOrg = resultingOrganizations.bySerialNumber()
            if (matchedOrg != null) {
                // report (name, importOrder, serialNumber, counter.notes)  if name or
                // add reading (in chronological order!!!!)
            } else {
                // add org to resultingOrganizations
            }
        }
        resultingOrganizations.check()
        TODO("Start with newer file (contains new organizations), but readings insert in chronological order (from older to newer)")
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


    companion object {
        fun List<OrganizationStructureUnit>.byId(id: Int) = first { it.id == id }
        fun List<Organization>.bySerialNumber(serialNumber: String) =
            firstOrNull { org -> org.counters.any { it.serialNumber == serialNumber } }

        private fun OrganizationCounterReadingRecord.toCounter(
            readingTime: LocalDateTime
        ): Counter {
            serialNumber.checkNotEmpty()
            return Counter(
                id = -1,
                serialNumber = serialNumber,
                organizationId = -1,
                K = K,
                readings = mutableListOf(
                    CounterReading(
                        id = -1,
                        counterId = -1,
                        reading = currentReading,
                        readingTime = readingTime,
                        user = "Саша",
                        comment = null,
                        synchronized = false,
                        syncTime = null,
                        serverId = null
                    )
                ),
                comment = notes,
                importOrder = importOrder
            )

        }

        private fun List<OrganizationCounterReadingRecord>.toOrganizations(
            orgStructureUnits: List<OrganizationStructureUnit>,
            readingTime: LocalDateTime
        ): List<Organization> {
            val organizationsWithOneCounter = filter { it.group == null }
                .map {
                    it.toOrganizationWithCounter(orgStructureUnits, readingTime)
                }
            val organizationsWithManyCounters = filter { it.group != null }
                .groupBy { it.group!! }
                .map { entry ->
                    entry.value.toOrganizationWithCounters(orgStructureUnits, readingTime)
                }
            return organizationsWithOneCounter + organizationsWithManyCounters
        }

        private fun OrganizationCounterReadingRecord.toOrganizationWithCounter(
            orgStructureUnits: List<OrganizationStructureUnit>,
            readingTime: LocalDateTime
        ): Organization {
            organizationName.checkNotEmpty()
            serialNumber.checkNotEmpty()
            return Organization(
                id = -1,
                orgStructureUnitId = orgStructureUnitId,
                orgStructureUnit = orgStructureUnits.byId(orgStructureUnitId),
                name = organizationName,
                counters = mutableListOf(toCounter(readingTime)),
                comment = null,
                importOrder = importOrder
            )

        }


        private fun List<OrganizationCounterReadingRecord>.toOrganizationWithCounters(
            orgStructureUnits: List<OrganizationStructureUnit>,
            readingTime: LocalDateTime
        ): Organization {
            checkIsTrue(orgStructureUnits.size > 1)
            return get(0).toOrganizationWithCounter(orgStructureUnits, readingTime)
                .apply {
                    counters.addAll(
                        drop(1).map { it.toCounter(readingTime) }
                    )
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