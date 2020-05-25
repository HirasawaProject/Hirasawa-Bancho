package io.hirasawa.server.bancho.user

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.message.PrivateChatMessage
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.objects.BanchoStatus
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.permissions.PermissionGroup
import java.util.*
import java.util.concurrent.TimeUnit

open class BanchoUser(id: Int, username: String, timezone: Byte, countryCode: Byte,
                      permissionGroups: ArrayList<PermissionGroup>, longitude: Float, latitude: Float,
                      var uuid: UUID, isBanned: Boolean) : User(id, username,
        timezone, countryCode, permissionGroups, longitude, latitude, isBanned) {
    val packetCache = Stack<BanchoPacket>()
    var status = BanchoStatus()
    var userStats = UserStats(id)
    var lastKeepAlive = 0
    val clientPermissions by lazy { Hirasawa.permissionEngine.calculateClientPermissions(this) }

    /**
     * Send a packet to the user
     *
     * @param banchoPacket The packet to send
     */
    open fun sendPacket(banchoPacket: BanchoPacket) {
        packetCache.push(banchoPacket)
    }

    /**\
     * Sets the lastKeepAlive value to the current time
     * This value is used to timeout the user after inactivity
      */
    fun updateKeepAlive() {
        lastKeepAlive = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()
    }

    /**
     * Sends a private chat message to this user
     *
     * @param from The user that sent the chat message
     * @param message The message sent to the user
     */
    override fun sendPrivateMessage(from: User, message: String) {
        Hirasawa.chatEngine.handleChat(PrivateChatMessage(from, this, message))
    }

    /**
     * Updates user stats for the specified gamemode
     *
     * This can be used to switch gamemodes or just update the stats on it
     */
    fun updateUserStats(gameMode: GameMode) {
        userStats = Hirasawa.database.getUserStats(this, gameMode) ?: return
    }



}