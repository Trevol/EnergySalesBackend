package database_creation.xlsx.reader

import com.tavrida.energysales.data_access.models.OrganizationStructureUnit
import com.tavrida.utils.noTrailingZero
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import kotlin.Exception

object OrganizationsWithStructureXlsReader {
    data class OrganizationCounterReadingRecord(
        val consumer: String,
        val prevReading: Double,
        val currentReading: Double,
        val K: Double,
        val serialNumber: String,
        val notes: String?,
        val group: Int?,
        val importOrder: Int,
        val orgStructureUnitId: Int
    )

    fun readOrganizations(path: File, firstRowContainsHeader: Boolean): List<OrganizationCounterReadingRecord> {
        return XSSFWorkbook(path).use { workbook ->
            val sheet = workbook.getSheetAt(0)
            with(OrganizationRow) {
                sequence {
                    var numOfEmptyRowsFound = 0
                    var order = 0


                    for ((index, row) in sheet.mapIndexed { index, row -> index to row }) {
                        if (firstRowContainsHeader && index == 0) {
                            continue
                        }
                        val sn = row.serialNumber()
                        // handle one-row separators between org units
                        if (sn.isNullOrEmpty()) {
                            if (numOfEmptyRowsFound == 0) {
                                numOfEmptyRowsFound = 1
                                continue
                            } else
                                break
                        } else {
                            numOfEmptyRowsFound = 0
                        }

                        yield(
                            OrganizationCounterReadingRecord(
                                consumer = row.organization(),
                                prevReading = row.prevReading(),
                                currentReading = row.currentReading(),
                                K = row.K(),
                                serialNumber = sn,
                                notes = row.notes(),
                                group = row.group(),
                                importOrder = ++order,
                                orgStructureUnitId = row.orgStructureUnitId()
                            )
                        )
                    }


                }.toList()
            }
        }
    }

    fun readOrgStructureUnits(path: File, firstRowContainsHeader: Boolean): List<OrganizationStructureUnit> {
        return XSSFWorkbook(path).use { workbook ->
            val sheet = workbook.getSheetAt(0)
            with(OrgStructureUnitRow) {
                sequence {
                    for ((index, row) in sheet.mapIndexed { index, row -> index to row }) {
                        if (firstRowContainsHeader && index == 0) {
                            continue
                        }
                        yield(
                            OrganizationStructureUnit(
                                id = row.id() ?: break,
                                parentId = row.parentId(),
                                name = row.name(),
                                comment = row.comment()
                            )
                        )
                    }
                }
            }.toList()
        }
    }

    private fun Row.cell(col: Int) = getCell(col)
    private fun Row.value(col: Int): Any? = cell(col)?.value()
    private fun Cell.value() = when (cellType) {
        CellType.NUMERIC -> this.numericCellValue
        CellType.STRING -> this.stringCellValue
        CellType.FORMULA -> this.numericCellValue
        CellType.BLANK -> this.stringCellValue
        else -> {
            throw Exception("Unexpected cellType: $cellType")
        }
    }

    fun Any?.cellToInt() = when {
        this == null -> null
        this == "" -> null
        this is Double -> toInt()
        this is String -> toInt()
        else -> throw Exception("Unexpected target $this")
    }

    private object OrganizationRow {
        private enum class Column {
            Organization, PrevReading, CurrentReading, K, SerialNumber, Notes, Group, OrgStructureUnitId
        }

        fun Row.organization(): String = value(Column.Organization.ordinal) as String
        fun Row.prevReading(): Double = value(Column.PrevReading.ordinal) as Double
        fun Row.currentReading(): Double = value(Column.CurrentReading.ordinal) as Double
        fun Row.K(): Double = value(Column.K.ordinal) as Double
        fun Row.serialNumber() = try {
            value(Column.SerialNumber.ordinal).let {
                if (it is Double) it.noTrailingZero() else it as String?
            }
        } catch (e: Exception) {
            throw e
        }

        fun Row.notes(): String? = value(Column.Notes.ordinal) as String?
        fun Row.group(): Int? = value(Column.Group.ordinal).cellToInt()
        fun Row.orgStructureUnitId() = value(Column.OrgStructureUnitId.ordinal).cellToInt()!!
    }

    private object OrgStructureUnitRow {
        private enum class Column {
            Id, Name, ParentId, Comment
        }

        fun Row.id() = value(Column.Id.ordinal).cellToInt()
        fun Row.name() = value(Column.Name.ordinal) as String
        fun Row.parentId() = value(Column.ParentId.ordinal).cellToInt()
        fun Row.comment() = value(Column.Comment.ordinal) as String
    }

}