package io.hirasawa.server.permissions

import io.hirasawa.server.bancho.user.User

class PermissionEngine {
    val permissionGroups = ArrayList<PermissionGroup>()

    fun createPermissionGroup(group: PermissionGroup) {
        permissionGroups.add(group)
    }

    fun removePermissionGroup(group: PermissionGroup) {
        permissionGroups.remove(group)
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

}