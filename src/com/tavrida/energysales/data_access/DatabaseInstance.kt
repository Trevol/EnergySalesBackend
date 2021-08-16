package com.tavrida.energysales.data_access

import com.tavrida.energysales.data_access.tables.allTables
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseInstance {
    fun get(directory: File, dbName: String) = dbUrl(directory, dbName).let {
        Database.connect(it)
        //.initSchema()
    }

    private var schemaInitialized = false
    private fun Database.initSchema(): Database {
        if (schemaInitialized) {
            return this
        }
        transaction(this) {
            SchemaUtils.create(*allTables)
        }
        schemaInitialized = true
        return this
    }

}

private fun dbUrl(directory: File, dbName: String) =
    File(directory, dbName)
        .let { dbPath ->
            "jdbc:h2:${dbPath.absolutePath}"
        }