package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable
import org.jetbrains.exposed.dao.id.IntIdTable

object ReportsTable: LaravelTable("reports") {
    val reporterId = integer("reporter_id").references(UsersTable.id)
    val userId = integer("user_id").references(UsersTable.id)
    val reason = varchar("reason", 255)
}