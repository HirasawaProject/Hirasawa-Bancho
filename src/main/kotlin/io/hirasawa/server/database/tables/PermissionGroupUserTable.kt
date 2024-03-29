package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable

object PermissionGroupUserTable: LaravelTable("permission_group_user") {
    val userId = integer("user_id").references(UsersTable.id)
    val groupId = integer("group_id").references(PermissionGroupsTable.id)
}