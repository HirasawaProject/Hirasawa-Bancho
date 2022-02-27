package io.hirasawa.server.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object PermissionGroupUsersTable: IntIdTable("permission_group_users") {
    val userId = integer("user_id")
    val groupId = integer("group_id").references(PermissionGroupsTable.id)
}