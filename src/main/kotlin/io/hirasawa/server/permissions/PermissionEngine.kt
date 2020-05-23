package io.hirasawa.server.permissions

import io.hirasawa.server.bancho.user.User

class PermissionEngine {
    val permissionGroups = HashMap<String, PermissionGroup>()

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
        return node in getPermissions(user)
    }

    fun calculateClientPermissions(user: User): Byte {
        var clientPermission = ClientPermission.NORMAL.byte.toInt()
        for (permission in getPermissions((user))) {
            when(permission) {
                "hirasawa.client.bat" -> {
                    clientPermission += ClientPermission.BAT.byte
                }
                "hirasawa.client.supporter" -> {
                    clientPermission += ClientPermission.SUPPORTER.byte
                }
                "hirasawa.client.moderator" -> {
                    clientPermission += ClientPermission.MODERATOR.byte
                }
                "hirasawa.client.admin" -> {
                    clientPermission += ClientPermission.ADMIN.byte
                }
            }
        }

        return clientPermission.toByte()
    }

}