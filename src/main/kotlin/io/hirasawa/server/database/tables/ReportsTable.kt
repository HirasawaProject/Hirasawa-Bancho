package io.hirasawa.server.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object ReportsTable: IntIdTable("reports") {
    val reporterId = integer("reporter_id").references(UsersTable.id)
    val userId = integer("user_id").references(UsersTable.id)
    val reason = varchar("reason", 255)
}