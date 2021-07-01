package database_creation.xlsx.reader

data class XlsRecord(
    val consumer: String,
    val prevReading: Double,
    val currentReading: Double,
    val K: Double,
    //val consumption: Double,
    val serialNumber: String,
    val notes: String?,
    val group: Int?,
    val importOrder: Int
)