package database_creation.xlsx.reader

import com.tavrida.utils.noTrailingZero
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.lang.Exception

object OrganizationsXlsReader {
    data class Record(
        val organization: String,
        val prevReading: Double,
        val currentReading: Double,
        val K: Double,
        //val consumption: Double,
        val serialNumber: String,
        val notes: String?,
        val group: Int?,
        val importOrder: Int
    )

    fun read(path: File): List<Record> {
        return XSSFWorkbook(path).use { workbook ->
            val sheet = workbook.getSheetAt(0)
            sequence {
                var order = 0
                for (row in sheet) {
                    val sn = row.serialNumber()
                    if (sn.isNullOrEmpty()) {
                        break
                    }
                    Record(
                        organization = row.organization(),
                        prevReading = row.prevReading(),
                        currentReading = row.currentReading(),
                        K = row.K(),
                        // consumption = row.consumption(),
                        serialNumber = sn,
                        notes = row.notes(),
                        group = row.group(),
                        importOrder = ++order,
                    ).also { yield(it) }
                }
            }.toList()
        }
    }

    private enum class Column {
        Organization, PrevReading, CurrentReading, K, /*Consumption,*/ SerialNumber, Notes, Group
    }

    private fun Row.cell(col: Column) = getCell(col.ordinal)
    private fun Row.value(col: Column): Any? = cell(col)?.value()

    private fun Row.organization(): String = value(Column.Organization) as String
    private fun Row.prevReading(): Double = value(Column.PrevReading) as Double
    private fun Row.currentReading(): Double = value(Column.CurrentReading) as Double
    private fun Row.K(): Double = value(Column.K) as Double
    // private fun Row.consumption(): Double = value(Column.Consumption) as Double
    private fun Row.serialNumber(): String? {
        try{

            val value = value(Column.SerialNumber)
            if (value is Double){
                return value.noTrailingZero()
            }
            return value as String?
        }
        catch (e: Exception){
            throw e
        }
    }
    private fun Row.notes(): String? = value(Column.Notes) as String?
    private fun Row.group(): Int? = (value(Column.Group) as Double?)?.toInt()

    private fun Cell.value(): Any {
        when {
            cellType == CellType.NUMERIC -> return this.numericCellValue
            cellType == CellType.STRING -> return this.stringCellValue
            cellType == CellType.FORMULA -> return this.numericCellValue
            cellType == CellType.BLANK -> return this.stringCellValue
            else -> {
                throw Exception("Unexpected cellType: $cellType")
            }
        }
    }
}