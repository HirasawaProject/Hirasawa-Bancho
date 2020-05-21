package io.hirasawa.server.bancho.user

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.message.PrivateChatMessage
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.objects.BanchoStatus
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.bancho.packets.BanchoPacket
import java.util.*
import java.util.concurrent.TimeUnit

open class BanchoUser(id: Int, username: String, timezone: Byte, countryCode: Byte, permissions: Byte, mode: GameMode,
                      longitude: Float, latitude: Float, var uuid: UUID, banned: Boolean) : User(id, username,
        timezone, countryCode, permissions, mode, longitude, latitude, banned) {
    val packetCache = Stack<BanchoPacket>()
    val userStats = UserStats(id, BanchoStatus(), 100, 10F, 100, 100, 1, 69)
    var lastKeepAlive = 0

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


}