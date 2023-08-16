package io.hirasawa.server.irc.threads

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.QuitReason
import io.hirasawa.server.plugin.event.chat.UserQuitEvent
import java.util.concurrent.TimeUnit

class IrcUserTimeoutThread: Runnable {
    override fun run() {
        val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()
        for (user in Hirasawa.irc.connectedUsers) {
            if (timestamp - user.lastKeepAlive >= Hirasawa.config.ircUserTimeout) {
                Hirasawa.irc.removeUser(user)

                UserQuitEvent(user, QuitReason.TIMEOUT).call().then {
                    Hirasawa.irc.removeUser(user)
                }
            }
        }
    }
}