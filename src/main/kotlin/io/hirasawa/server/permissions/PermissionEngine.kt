package io.hirasawa.server.permissions

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.database.tables.PermissionGroupsTable
import io.hirasawa.server.database.tables.PermissionNodesTable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionScope

class PermissionEngine(val permissionGroups: HashMap<String, PermissionGroup>) {
    init {
        this.reload()
    }

    fun addGroup(group: PermissionGroup) {
        permissionGroups[group.name] = group
    }

    fun removeGroup(group: PermissionGroup) {
        permissionGroups.remove(group.name)
    }

    fun removeGroup(group: String) {
        permissionGroups.remove(group)
    }

    fun getGroup(key: String): PermissionGroup {
        return permissionGroups[key]!!
    }

    fun getPermissions(user: User): ArrayList<String> {
        val permissions = ArrayList<String>()

        for (group in user.permissionGroups) {
            for (permission in group.permissions) {
                permissions.add(permission)
            }
        }

        return permissions
    }

    fun hasPermission(user: User, node: String): Boolean {
        if (node.isEmpty()) return true
        return node in getPermissions(user)
    }

    fun calculateClientPermissions(user: User): Byte {
        var clientPermissions = ClientPermission.NORMAL.byte.toInt()
        for (permission in getPermissions((user))) {
            when(permission) {
                "hirasawa.client.bat" -> {
                    clientPermissions += ClientPermission.BAT.byte
                }
                "hirasawa.client.supporter" -> {
                    clientPermissions += ClientPermission.SUPPORTER.byte
                }
                "hirasawa.client.moderator" -> {
                    clientPermissions += ClientPermission.MODERATOR.byte
                }
                "hirasawa.client.admin" -> {
                    clientPermissions += ClientPermission.ADMIN.byte
                }
            }
        }

        return clientPermissions.toByte()
    }

    fun reload() {
        permissionGroups.clear()
        transaction {
            (PermissionNodesTable leftJoin PermissionGroupsTable).selectAll().forEach {
                if (it[PermissionGroupsTable.name] !in permissionGroups.keys) {
                    permissionGroups[it[PermissionGroupsTable.name]] = PermissionGroup(it[PermissionGroupsTable.name])
                }
                permissionGroups[it[PermissionGroupsTable.name]]?.addPermission(it[PermissionNodesTable.node])
            }
        }
    }

}