package io.hirasawa.server.database.tables

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

object PermissionNodesTable: IntIdTable("permission_nodes") {
    val node = varchar("node", 255)
    val groupId = integer("group_id").references(PermissionGroupsTable.id)
}