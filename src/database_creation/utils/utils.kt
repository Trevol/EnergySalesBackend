package database_creation.utils

import com.tavrida.energysales.data_access.models.DataContext
import database_creation.DbInstance
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Any.log() = println(this)

fun Any.padStartEx(length: Int, padChar: Char) = toString().padStart(length, padChar)

fun deleteFileInDir(dbDir: String, startsWith: String) {
    File(dbDir)
        .listFiles { dir, name -> name.startsWith(startsWith, true) }!!
        .forEach { it.delete() }
}

fun Any?.println() = println(this)
fun Any?.print() = print(this)
fun Any?.print(prefix: String = "", suffix: String = "") = "$prefix$this$suffix".print()


fun currentDateStamp(): String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

fun dataContextWithTimestampedDb(dbNameSuffix: String): DataContext {
    val currentDateStamp = currentDateStamp()
    val dbDir = "./databases/$currentDateStamp".also {
        File(it).mkdirs()
    }
    val dbName = "ENERGY_SALES_$currentDateStamp$dbNameSuffix"
    return DbInstance(dbDir, dbName)
        .get(recreate = true)
        .let { DataContext(it) }
}