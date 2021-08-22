package com.tavrida.energysales.server.services

import com.tavrida.energysales.data_access.models.*
import com.tavrida.utils.noTrailingZero
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.html.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class CounterReadingUIController(private val dataContext: DataContext) {
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
                    "Наименование" to { it.organizationName },
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
                    "Время" to {
                        style = "text-align:center"
                        it.currentReadingDateTime
                    },
                    "Расход [(Наст-Пред)*K]" to {
                        style = "text-align:center"
                        it.currentConsumption
                    },
                    "Пред. Расход" to {
                        style = "text-align:center"
                        it.prevConsumption
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
        val fn = "Распределение.xlsx"
        call.response.header("Content-Disposition", "attachment; filename=\"$fn\"")
        readAllRecsAsWorkbook().use { wb ->
            call.respondOutputStream(contentType = xlsxContentType) {
                wb.write(this)
            }
        }
    }

    private fun readAllRecsAsWorkbook(): XSSFWorkbook {
        val items = dataContext.loadReadings()
        val wb = XSSFWorkbook()
        val sheet = wb.createSheet("Распределение")

        val config: List<Pair<String, (CounterReadingRecord) -> Any>> = listOf(
            "#" to { it.importOrder },
            "Наименование" to { it.organizationName },
            "К трансф" to {
                it.K
            },
            "Предыд. показ" to {
                it.prevReading
            },
            "Настоящ. показ" to {
                it.currentReading
            },
            /*"Время" to {
                style = "text-align:center"
                it.currentReadingDateTime
            },*/
            "Наст. Расход [(Наст-Пред)*K]" to {
                it.currentConsumption
            },
            "Пред. Расход" to {
                it.prevConsumption
            },
            "Заводской № электросчетчика" to {
                it.serialNumber
            },
            "Примечание" to { it.comment }
        )

        val colRow = sheet.createRow(0)
        config.map { (col, valFn) -> col }.forEachIndexed() { i, col ->
            colRow.createCell(i).setCellValue(col)
        }
        val cellValues = config.map { (col, valFn) -> valFn }
        items.forEachIndexed { rowIndex, rec ->
            val row = sheet.createRow(rowIndex + 1)
            cellValues.forEachIndexed { colIndex, cellValue ->
                row.createCell(colIndex).setCellValue(cellValue(rec).toString())
            }
        }
        return wb
    }

    private companion object {
        val xlsxContentType = ContentType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")

        private data class CounterReadingRecord(
            val importOrder: Int,
            val organizationName: String,
            val prevReading: String,
            val currentReading: String,
            val currentReadingDateTime: String,
            val K: Int,
            val currentConsumption: String,
            val prevConsumption: String,
            val serialNumber: String,
            val comment: String
        )

        private fun DataContext.loadReadings(): List<CounterReadingRecord> = let { dc ->
            transaction(dc) {
                dc.loadAllOrganizations()
                    .flatMap { organization ->
                        organization.counters.map { counter ->
                            toRecord(organization, counter)
                        }
                    }
                // уже должен быть отсортирован по importOrder
            }
        }

        private fun toRecord(organization: Organization, counter: Counter): CounterReadingRecord {
            val recentReading = counter.recentReading
            return CounterReadingRecord(
                importOrder = counter.importOrder,
                organizationName = organization.name,
                prevReading = counter.prevReading(LocalDate.now())?.reading.noTrailingZero(),
                currentReading = recentReading?.reading.noTrailingZero(),
                currentReadingDateTime = recentReading?.readingTime?.format(dateTimeFormatter).orEmpty(),
                K = counter.K.toInt(),
                currentConsumption = counter.currentConsumption()?.roundToInt()?.toString().orEmpty(),
                prevConsumption = "", // TODO: counter.prevReading.consumption.roundToInt().toString(),
                //.toDouble().noTrailingZero(),
                serialNumber = counter.serialNumber,
                comment = counter.comment.orEmpty(),
            )
        }
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
