package database_creation.xlsx

import database_creation.utils.println
import java.io.File
import java.time.LocalDateTime
import java.time.Month

typealias TimeAndFile = Pair<LocalDateTime, File>

fun main() {
    val baseDir = File("./databases/xlsx/import_all")
    val config = ImportConfig(
        corpusFile = File(baseDir, "иерархия_организаций.xlsx"),
        readingsAndTime = listOf(
            LocalDateTime.of(2021, Month.JULY, 1, 11, 30, 33) to File(baseDir, "import 21.06.xlsx"),
            // LocalDateTime.of(2021, Month.JUNE, 1, 12, 22, 15) to File(baseDir, "import 21.05.xlsx")
        )
    )


}


private data class ImportConfig(
    val corpusFile: File,
    val readingsAndTime: List<TimeAndFile>
)


fun TODO_ITEMS() {
    /*
    - подготовить показания по месяцам
        - ассоциировать файл (или worksheet в файле) с временной меткой сбора показаний
    - корпуса
        - таблица + сущность  (плоская или иерархия?)
        - прописать в экселях
    * */
}