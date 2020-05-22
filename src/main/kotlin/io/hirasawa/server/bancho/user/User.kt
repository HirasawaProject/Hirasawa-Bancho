package io.hirasawa.server.bancho.user

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.command.CommandSender
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.permissions.PermissionGroup

/**
 * A base user, this is used to extend into classes with more features
 */
abstract class User(val id: Int, val username: String, val timezone: Byte, val countryCode: Byte,
                    val permissionGroups: ArrayList<PermissionGroup>, val mode: GameMode, val longitude: Float,
                    val latitude: Float, val isBanned: Boolean): CommandSender {
    abstract fun sendPrivateMessage(from: User, message: String)

    fun hasPermission(node: String): Boolean {
        return Hirasawa.permissionEngine.hasPermission(this, node)
    }
}