package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object PermissionGroupsTable: LaravelTable("permission_groups") {
    val name = varchar("name", 255)
}