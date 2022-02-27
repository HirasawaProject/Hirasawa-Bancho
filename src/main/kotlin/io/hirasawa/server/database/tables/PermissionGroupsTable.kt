package io.hirasawa.server.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object PermissionGroupsTable: IntIdTable("permission_groups") {
    val name = varchar("name", 255)
}