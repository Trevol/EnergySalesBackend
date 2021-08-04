package database_creation.xlsx

import com.tavrida.energysales.data_access.models.DataContext
import database_creation.utils.dataContextWithTimestampedDb
import database_creation.utils.println
import database_creation.xlsx.reader.OrganizationsWithStructureXlsReader
import java.io.File
import java.time.LocalDateTime
import java.time.Month

typealias TimeAndFile = Pair<LocalDateTime, File>

fun main() {
    val baseDir = File("./databases/xlsx/import_all")
    val config = ImportConfig(
        orgStructureFile = File(baseDir, "org_units.xlsx"),
        timeToReadings = listOf(
            LocalDateTime.of(2021, Month.JULY, 1, 11, 30, 33) to File(baseDir, "import 21.06.xlsx"),
            // LocalDateTime.of(2021, Month.JUNE, 1, 12, 22, 15) to File(baseDir, "import 21.05.xlsx")
        )
    )

    val dc = dataContextWithTimestampedDb(dbNameSuffix = "_xls_ALL")
    Importer(config).import(dc)
}


private data class ImportConfig(
    val orgStructureFile: File,
    val timeToReadings: List<TimeAndFile>
)


private class Importer(val config: ImportConfig) {
    fun import(dataContext: DataContext) {

        for ((time, readingsFile) in config.timeToReadings) {
            val organizations = OrganizationsWithStructureXlsReader.readOrganizations(
                readingsFile,
                firstRowContainsHeader = true
            )
            organizations.size.println()
        }

        // TODO("Not yet implemented")
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