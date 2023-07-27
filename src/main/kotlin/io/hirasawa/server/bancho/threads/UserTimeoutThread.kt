package io.hirasawa.server.bancho.threads

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.QuitReason
import io.hirasawa.server.bancho.enums.QuitState
import io.hirasawa.server.bancho.packets.HandleUserQuitPacket
import io.hirasawa.server.plugin.event.chat.UserQuitEvent
import java.util.concurrent.TimeUnit

class UserTimeoutThread: Runnable {
    override fun run() {
        val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()
        for (user in Hirasawa.banchoUsers) {
            if (timestamp - user.lastKeepAlive >= Hirasawa.config.banchoUserTimeout) {
                Hirasawa.sendBanchoPacketToAll(HandleUserQuitPacket(user, QuitState.GONE))

                UserQuitEvent(user, QuitReason.TIMEOUT).call().then {
                    Hirasawa.banchoUsers.remove(user)
                    Hirasawa.multiplayer.handleUserDisconnect(user)
                }

            }
        }
    }
}