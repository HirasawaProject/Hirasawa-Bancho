package io.hirasawa.server.permissions

data class PermissionGroup(val name: String) {
    val permissions = ArrayList<String>()

    operator fun contains(node: String): Boolean {
        return permissions.contains(node)
    }
}