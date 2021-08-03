package database_creation.utils

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