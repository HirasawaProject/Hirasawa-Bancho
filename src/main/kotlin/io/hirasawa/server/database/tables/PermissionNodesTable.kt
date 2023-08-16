package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object PermissionNodesTable: LaravelTable("permission_nodes") {
    val node = varchar("node", 255)
    val groupId = integer("group_id").references(PermissionGroupsTable.id)
}