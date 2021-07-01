package database_creation.xlsx.reader

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.lang.Exception

object ImportXlsReader {
    fun read(path: String): List<XlsRecord> {
        return XSSFWorkbook(path).use { workbook ->
            val sheet = workbook.getSheetAt(0)
            sequence {
                var order = 0
                for (row in sheet) {
                    val sn = row.serialNumber()
                    if (sn.isNullOrEmpty()) {
                        break
                    }
                    XlsRecord(
                        consumer = row.consumer(),
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
        Consumer, PrevReading, CurrentReading, K, /*Consumption,*/ SerialNumber, Notes, Group
    }

    private fun Row.cell(col: Column) = getCell(col.ordinal)
    private fun Row.value(col: Column): Any? = cell(col)?.value()

    private fun Row.consumer(): String = value(Column.Consumer) as String
    private fun Row.prevReading(): Double = value(Column.PrevReading) as Double
    private fun Row.currentReading(): Double = value(Column.CurrentReading) as Double
    private fun Row.K(): Double = value(Column.K) as Double
    // private fun Row.consumption(): Double = value(Column.Consumption) as Double
    private fun Row.serialNumber(): String? {
        try{

            return value(Column.SerialNumber) as String?
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