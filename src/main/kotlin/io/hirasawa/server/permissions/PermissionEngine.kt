package io.hirasawa.server.permissions

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.User

class PermissionEngine {
    val permissionGroups = Hirasawa.database.getPermissionGroups()

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

}