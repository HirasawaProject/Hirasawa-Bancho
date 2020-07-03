package io.hirasawa.server.database.tables

import org.jetbrains.exposed.sql.Table

object PermissionGroupsTable: Table("permission_groups") {
    val name = varchar("name", 255)
}