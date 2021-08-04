package com.tavrida.energysales.data_access.dbmodel.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime

object OrganizationStructureUnits : IdTable<Int>("PUBLIC.ORGANIZATION_STRUCTURE") {
    override val id = integer("id").entityId()
    override val primaryKey = PrimaryKey(id)

    // val parentId = reference("parent_id", OrganizationStructureUnits).nullable()
    val parentId = integer("parent_id").references(OrganizationStructureUnits.id).nullable()
    val name = varchar("name", 256).uniqueIndex()
    val comment = varchar("comment", 2000).nullable()
}

object OrganizationsTable : IntIdTable("PUBLIC.ORGANIZATION") {
    val orgStructureUnitId = reference("org_structure_unit_id", OrganizationStructureUnits)
    val name = varchar("name", 256).uniqueIndex()
    val comment = varchar("comment", 2000).nullable()
    val importOrder = integer("import_order").uniqueIndex()
}

object CountersTable : IntIdTable("PUBLIC.COUNTER") {
    val serialNumber = varchar("serial_number", 20).uniqueIndex()
    val organizationId = reference("organization_id", OrganizationsTable.id)
    val K = double("K")
    val comment = varchar("comment", 2000).nullable()
    val importOrder = integer("import_order").uniqueIndex()
}

object CounterReadingsTable : IntIdTable("PUBLIC.COUNTER_READING") {
    val counterId = reference("counter_id", CountersTable.id)
    val reading = double("reading")
    val readingTime = datetime("reading_time")
    val user = varchar("user", 256)
    val comment = varchar("comment", 2000).nullable()
    val synchronized = bool("synchronized")
    val syncTime = datetime("syncTime").nullable()
    val serverId = integer("server_id").nullable()
}

val allTables =
    arrayOf(OrganizationStructureUnits, OrganizationsTable, CountersTable, CounterReadingsTable)
