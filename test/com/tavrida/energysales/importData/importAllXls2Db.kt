package com.tavrida.energysales.importData

import com.tavrida.energysales.data_access.tables.OrganizationStructureUnits
import com.tavrida.energysales.data_access.models.*
import database_creation.utils.checkIsTrue
import database_creation.utils.checkNotEmpty
import database_creation.xlsx.reader.OrganizationsWithStructureXlsReader
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import database_creation.utils.println
import com.tavrida.energysales.importData.OrganizationChecker.check
import com.tavrida.energysales.importData.OrganizationChecker.checkSerialNumberDuplicates
import database_creation.DbInstance
import io.kotest.matchers.collections.shouldNotBeEmpty
import org.junit.Test
import java.io.File
import java.time.LocalDateTime
import java.time.Month

typealias TimeAndFile = Pair<LocalDateTime, File>
typealias OrganizationCounterReadingRecord = OrganizationsWithStructureXlsReader.OrganizationCounterReadingRecord

class ImportAllXlsToDb {
    @Test
    fun import() {
        val baseDir = File("./databases/xlsx/import_all")
        val config = Importer.ImportConfig(
            orgStructureFile = File(baseDir, "org_units.xlsx"),
            timeToReadings = listOf(
                LocalDateTime.of(2021, Month.JULY, 1, 11, 30, 33) to File(baseDir, "import 21.06.xlsx"),
                LocalDateTime.of(2021, Month.JUNE, 1, 12, 22, 15) to File(baseDir, "import 21.05.xlsx"),
                LocalDateTime.of(2021, Month.APRIL, 29, 11, 45, 33) to File(baseDir, "import 21.04.xlsx"),
                LocalDateTime.of(2021, Month.APRIL, 1, 10, 51, 1) to
                        File(baseDir, "import 21.03_copy_paste_from_21.04.xlsx")
            )
        )

        /*val orgAndStruct = Importer(config).xlsToOrganizationsAndStructure()
        orgAndStruct.structure.size.println()
        orgAndStruct.organizations.size.println()
        orgAndStruct.organizations.flatMap { it.counters }.size.println()*/

        val dc = DbInstance(baseDir, "ENERGY_SALES_xls_ALL")
            .get(recreate = true)
            .let { DataContext(it) }
        // val dc = dataContextWithTimestampedDb(databasesDir = "./databases", dbNameSuffix = "xls_ALL")

        Importer(config).xlsToOrganizationsAndStructureToDb(dc)
        dc.loadAllOrganizations().run {
            size.println()
            flatMap { it.counters }.size.println()
        }
    }

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
                "Importing: $time <-> $readingsFile".println()
                time to OrganizationsWithStructureXlsReader.readOrganizations(
                    readingsFile,
                    firstRowContainsHeader = true
                ).also { it.checkSerialNumberDuplicates() }
            }
            .forEach { (readingTime, orgCounterReadingRecs) ->
                val currentOrganizations = orgCounterReadingRecs.toOrganizations(orgStructureUnits, readingTime)
                currentOrganizations.check()
                mergeOrganizationsReadings(resultingOrganizations, currentOrganizations)
            }

        return OrganizationsAndStructure(resultingOrganizations, orgStructureUnits)
    }

    fun mergeOrganizationsReadings(
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
            checkIsTrue(currentOrg.counters.size >= 1)
            val matchResult = OrganizationsMatchResult.tryMatch(resultingOrganizations, currentOrg)
            if (matchResult == null) {
                resultingOrganizations.add(currentOrg)
                "Organization ${currentOrg.name} added".log()
            } else {
                matchResult.warning?.log()
                matchResult.matchedCounters.shouldNotBeEmpty()
                matchResult.matchedCounters.forEach {
                    it.matched.copyReadingsFrom(it.current)
                }
            }
        }
        resultingOrganizations.check()
    }

    fun Counter.copyReadingsFrom(from: Counter) {
        // TODO("Start with newer file (contains new organizations), but readings insert in chronological order (from older to newer)")
        //add reading (in chronological order!!!!)
        readings.addAll(0, from.readings)
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

private data class OrganizationsMatchResult(
    val matched: Organization,
    val current: Organization,
    val matchedCounters: List<CounterMatchResult>,
    val warning: String?
) {
    data class CounterMatchResult(val matched: Counter, val current: Counter)

    companion object {
        fun tryMatch(
            resultingOrganizations: MutableList<Organization>,
            currentOrg: Organization
        ): OrganizationsMatchResult? {
            resultingOrganizations.forEach { matchedOrg ->
                val countersMatch = matchCounters(matchedOrg, currentOrg)
                if (countersMatch.isNotEmpty()) {
                    var warning: String? = null
                    val countersMatchWithCommentDiff =
                        countersMatch.filter { it.current.comment != it.matched.comment }
                            .map { "[${it.matched.comment}] <-> [${it.current.comment}]" }
                    if (matchedOrg.name != currentOrg.name || countersMatchWithCommentDiff.isNotEmpty())
                        warning = "Org.Name or counters.comment diff. ${matchedOrg.name} <-> ${currentOrg.name}. " +
                                "$countersMatchWithCommentDiff"
                    return OrganizationsMatchResult(matchedOrg, currentOrg, countersMatch, warning)
                }
            }
            resultingOrganizations.forEach { matchedOrg ->
                if (matchedOrg.name == currentOrg.name) {
                    // совпадение по имени или по примечанию счетчика
                    throw Exception("${matchedOrg.name}: Нет совпадения по счетчикам, но есть совпадение по имени")
                }
                /*val counterWithCommentMatch = matchedOrg.counters.firstOrNull { matchedCounter ->
                    currentOrg.counters.any { it.comment == matchedCounter.comment }
                }
                if (counterWithCommentMatch != null) {
                    throw Exception("${matchedOrg.name} <-> ${currentOrg.name}: нет совпадения по Sn, но есть совпадение по counter.comment: $counterWithCommentMatch")
                }*/
            }
            return null
        }

        private fun matchCounters(
            matchedOrg: Organization,
            currentOrg: Organization
        ): List<CounterMatchResult> {
            val result = mutableListOf<CounterMatchResult>()
            matchedOrg.counters.forEach { matchingCounter ->
                val currentCounter = currentOrg.counters.bySerialNumber(matchingCounter.serialNumber)
                if (currentCounter != null) {
                    result.add(CounterMatchResult(matchingCounter, currentCounter))
                }
            }
            if (result.isEmpty()) {
                return result
            }
            val partialMatching = matchedOrg.counters.size != currentOrg.counters.size ||
                    result.size != matchedOrg.counters.size
            if (partialMatching) {
                throw Exception("Partial counter matching detected. Matching org: ${matchedOrg.name}. Current org: ${currentOrg.name}")
            }
            return result
        }

        private fun List<Counter>.bySerialNumber(serialNumber: String) =
            firstOrNull { it.serialNumber == serialNumber }
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