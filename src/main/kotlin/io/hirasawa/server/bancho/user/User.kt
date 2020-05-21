package io.hirasawa.server.bancho.user

import io.hirasawa.server.bancho.chat.command.CommandSender
import io.hirasawa.server.bancho.enums.GameMode

/**
 * A base user, this is used to extend into classes with more features
 */
abstract class User(val id: Int, val username: String, val timezone: Byte, val countryCode: Byte, val permissions: Byte,
                    val mode: GameMode, val longitude: Float, val latitude: Float, val isBanned: Boolean): CommandSender {
    abstract fun sendPrivateMessage(from: User, message: String)
}