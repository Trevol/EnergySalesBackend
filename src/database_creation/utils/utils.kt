package database_creation.utils

import com.tavrida.energysales.data_access.models.DataContext
import database_creation.DbInstance
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun Any.log() = println(this)

fun Any.padStartEx(length: Int, padChar: Char) = toString().padStart(length, padChar)

fun deleteFileInDir(dir: String, startsWith: String) {
    deleteFileInDir(File(dir), startsWith)
}

fun deleteFileInDir(dir: File, startsWith: String) {
    dir.listFiles { _, name -> name.startsWith(startsWith, true) }!!
        .forEach { it.delete() }
}

fun Any?.println() = println(this)
fun Any?.print() = print(this)
fun Any?.print(prefix: String = "", suffix: String = "") = "$prefix$this$suffix".print()


fun currentDateStamp(): String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

fun dataContextWithTimestampedDb(
    databasesDir: String,
    dbNamePrefix: String = "ENERGY_SALES",
    dbNameSuffix: String
) = timestampedDb(databasesDir, dbNamePrefix, dbNameSuffix).let { DataContext(it) }

fun timestampedDb(
    databasesDir: String,
    dbNamePrefix: String = "ENERGY_SALES",
    dbNameSuffix: String
): Database {
    val currentDateStamp = currentDateStamp()
    val dbDir = File(databasesDir, currentDateStamp).also {
        it.mkdirs()
    }
    val dbName = "${dbNamePrefix}_${currentDateStamp}_$dbNameSuffix"
    return DbInstance(dbDir, dbName)
        .get(recreate = true)
}

inline fun checkNotEmpty(value: String) {
    if (value.isEmpty()) {
        throw AssertionError("value is empty")
    }
}

@JvmName("checkNotEmptyExt")
inline fun String.checkNotEmpty() = checkNotEmpty(this)

inline fun <T> checkNotEmpty(value: Collection<T>) {
    if (value.isEmpty()) {
        throw AssertionError("value is empty")
    }
}

@JvmName("checkNotEmptyExt")
inline fun <T> Collection<T>.checkNotEmpty() = checkNotEmpty(this)

inline fun checkIsTrue(value: Boolean){
    if (!value){
        throw AssertionError("value should be true")
    }
}