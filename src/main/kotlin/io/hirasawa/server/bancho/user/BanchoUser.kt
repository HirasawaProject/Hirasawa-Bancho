package io.hirasawa.server.bancho.user

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.objects.BanchoStatus
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.bancho.packets.BanchoPacket
import java.util.*
import java.util.concurrent.TimeUnit

class BanchoUser(id: Int, username: String, timezone: Byte, countryCode: Byte, permissions: Byte, mode: GameMode,
                 longitude: Float, latitude: Float, rank: Int, var uuid: UUID) : User(id, username,
        timezone, countryCode, permissions, mode, longitude, latitude, rank) {
    val packetCache = Stack<BanchoPacket>()
    val userStats = UserStats(id, BanchoStatus(), 100, 10F, 100, 100, 1, 69)
    var lastKeepAlive = 0

    override fun onMessage(from: User, channel: String, message: String) {
        // TODO send message
    }

    /**
     * Send a packet to the user
     *
     * @param banchoPacket The packet to send
     */
    fun sendPacket(banchoPacket: BanchoPacket) {
        packetCache.push(banchoPacket)
    }

    /**
     * Sets the lastKeepAlive value to the current time
     * This value is used to timeout the user after inactivity
      */
    fun updateKeepAlive() {
        lastKeepAlive = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()
    }


}