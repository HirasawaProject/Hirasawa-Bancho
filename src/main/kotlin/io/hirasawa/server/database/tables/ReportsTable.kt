package io.hirasawa.server.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object ReportsTable: IntIdTable("reports") {
    val reporterId = integer("reporter_id").references(UsersTable.id)
    val reporteeId = integer("reportee_id").references(UsersTable.id)
    val reason = varchar("reason", 255)
}