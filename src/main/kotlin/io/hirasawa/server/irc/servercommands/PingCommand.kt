package io.hirasawa.server.irc.servercommands

import io.hirasawa.server.irc.clientcommands.Pong
import io.hirasawa.server.irc.objects.IrcUser

class PingCommand: IrcServerCommand {
    override fun handle(user: IrcUser, command: String, args: Array<String>) {
        user.sendReply(Pong())
    }
}