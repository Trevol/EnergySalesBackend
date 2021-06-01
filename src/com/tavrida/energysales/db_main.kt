package com.tavrida.energysales

import com.tavrida.energysales.data_access.DatabaseInstance
import com.tavrida.energysales.data_access.dbmodel.tables.ConsumersTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun main() {
    val db = DatabaseInstance.get(File("db").absoluteFile)
    transaction(db){
        ConsumersTable.selectAll().forEach {

        }
    }
}