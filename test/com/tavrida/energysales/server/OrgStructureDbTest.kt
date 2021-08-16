package com.tavrida.energysales.server

import com.tavrida.energysales.data_access.tables.OrganizationStructureUnits
import database_creation.DbInstance
import database_creation.utils.println
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test
import java.io.File

class OrgStructureDbTest {
    @Test
    fun uniqueNameTest() {
        val dbDir = File("./databases/test_tmp").also { it.mkdirs() }
        val db = DbInstance(dbDir, "energy_sales_test_tmp").get(recreate = true)
        transaction(db) {
            OrganizationStructureUnits.insert {
                it[id] = 1
                it[parentId] = null
                it[name] = "Root"
            }
            OrganizationStructureUnits.insert {
                it[id] = 2
                it[parentId] = 1
                it[name] = "Node1.1"
            }

            OrganizationStructureUnits.insert {
                it[id] = 3
                it[parentId] = 1
                it[name] = "Node1.1"
            }

            OrganizationStructureUnits.selectAll().toList().println()
        }
    }
}