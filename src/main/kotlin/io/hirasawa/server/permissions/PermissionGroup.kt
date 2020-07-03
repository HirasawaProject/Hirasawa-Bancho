package io.hirasawa.server.permissions

import io.hirasawa.server.database.tables.PermissionGroupsTable
import org.jetbrains.exposed.sql.ResultRow

data class PermissionGroup(val name: String) {
    constructor(result: ResultRow): this(result[PermissionGroupsTable.name])
    val permissions = ArrayList<String>()

    operator fun contains(node: String): Boolean {
        return permissions.contains(node)
    }

    fun addPermission(node: String) {
        permissions.add(node)
    }

    fun removePermission(node: String) {
        permissions.remove(node)
    }
}