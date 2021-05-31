package com.tavrida.energysales.server

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.Database
import java.io.File

class CounterReadingUIController(val db: Database) {
    fun indexView(html: HTML) {
        html.run {
            head {
                title("Показания")
            }
            body {
                div {
                    +"It is Readings"
                }
                a(href = "/download", target = "_blank") {
                    +"Загрузить"
                }
            }
        }
    }

    suspend fun downloadAsXlsx(call: ApplicationCall) {
        // val xlsxFile = File("/hdd/TMP/Распределение 2021/import.xlsx")
        val xlsxFile = File("/hdd/TMP/Распределение 2021/tmp.xlsx")
        call.response.header("Content-Disposition", "attachment; filename=\"${xlsxFile.name}\"")

        call.respondOutputStream(
            contentType = xlsxContentType
        ) {
            val inputStream = xlsxFile.inputStream()
            inputStream.use {
                it.copyTo(this)
            }
        }
    }

    private companion object {
        // "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        val xlsxContentType = ContentType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    }
}