package io.hirasawa.server.irc.servercommands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.QuitReason
import io.hirasawa.server.irc.clientcommands.Pong
import io.hirasawa.server.irc.clientcommands.RplList
import io.hirasawa.server.irc.clientcommands.RplListEnd
import io.hirasawa.server.irc.clientcommands.RplListStart
import io.hirasawa.server.irc.objects.IrcUser
import io.hirasawa.server.plugin.event.chat.UserQuitEvent

class QuitCommand: IrcServerCommand {
    override fun handle(user: IrcUser, command: String, args: Array<String>) {
        UserQuitEvent(user, QuitReason.QUIT).call()
        Hirasawa.irc.removeUser(user)
    }
}