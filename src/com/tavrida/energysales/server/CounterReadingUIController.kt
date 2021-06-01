package com.tavrida.energysales.server

import com.tavrida.energysales.data_access.models.*
import com.tavrida.utils.noTrailingZero
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.html.*
import org.jetbrains.exposed.sql.Database
import java.io.File
import java.time.format.DateTimeFormatter

class CounterReadingUIController(db: Database) {
    private val dataContext = DataContext(db)

    fun indexView(html: HTML) {
        html.run {
            head {
                title("Показания")
            }
            body {
                a(href = "/download", target = "_blank") {
                    +"Загрузить"
                }
                val items = dataContext.loadReadings()

                tableFor(items,
                    "#" to { it.importOrder },
                    "Наименование" to { it.consumerName },
                    "К трансф" to {
                        style = "text-align:center"
                        it.K
                    },
                    "Предыд. показ" to {
                        style = "text-align:center"
                        it.prevReading
                    },
                    "Настоящ. показ" to {
                        style = "text-align:center"
                        it.currentReading
                    },
                    /*"Время" to {
                        style = "text-align:center"
                        it.currentReadingDateTime
                    },*/
                    "Расход" to {
                        style = "text-align:center"
                        it.consumption
                    },
                    "Заводской № электросчетчика" to {
                        style = "text-align:center"
                        it.serialNumber
                    },
                    "Примечание" to { it.comment }
                )

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
            xlsxFile.inputStream().use {
                it.copyTo(this)
            }
        }
    }

    private companion object {
        val xlsxContentType = ContentType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

        private data class CounterReadingRecord(
            val importOrder: Int,
            val consumerName: String,
            val prevReading: String,
            val currentReading: String,
            val currentReadingDateTime: String,
            val K: Int,
            val consumption: String,
            val serialNumber: String,
            val comment: String
        )

        private fun DataContext.loadReadings(): List<CounterReadingRecord> = let { dc ->
            transaction(dc) {
                dc.loadAll()
                    .flatMap { consumer ->
                        consumer.counters.map { counter ->
                            toRecord(consumer, counter)
                        }
                    }
                // уже должен быть отсортирован
                //.sortedBy { it.importOrder }
            }
        }

        private fun toRecord(consumer: Consumer, counter: Counter) =
            CounterReadingRecord(
                importOrder = counter.importOrder,
                consumerName = consumer.name,
                prevReading = counter.prevReading.reading.noTrailingZero(),
                currentReading = counter.lastReading?.reading.noTrailingZero(),
                currentReadingDateTime = counter.lastReading?.readingTime?.format(dateTimeFormatter) ?: "",
                K = counter.K.toInt(),
                consumption = counter.lastConsumption().let {
                    if (it == null) {
                        ""
                    } else {
                        if (it < 0) {
                            "${it.noTrailingZero()} (через 0)"
                        } else {
                            it.noTrailingZero()
                        }
                    }
                },
                serialNumber = counter.serialNumber,
                comment = counter.comment.orEmpty(),
            )
    }
}

fun <TItem> FlowContent.tableFor(
    items: List<TItem>, requiredCol: Pair<String, TD.(TItem) -> Any>,
    vararg cols: Pair<String, TD.(TItem) -> Any>
) {
    listOf(requiredCol, *cols)
        .also { cols ->
            table {
                thead {
                    tr {
                        for ((colName) in cols) {
                            th { +colName }
                        }
                    }
                }
                tbody {
                    for (item in items) {
                        tr {
                            for ((_, value) in cols) {
                                td {
                                    +value(item).toString()
                                }
                            }
                        }
                    }
                }
            }
        }

}
