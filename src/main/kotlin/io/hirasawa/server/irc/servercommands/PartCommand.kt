package io.hirasawa.server.irc.servercommands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.irc.clientcommands.*
import io.hirasawa.server.irc.objects.IrcUser

class PartCommand: IrcServerCommand {
    override fun handle(user: IrcUser, command: String, args: Array<String>) {
        val channel = Hirasawa.chatEngine[args[0]]
        if (channel == null) {
            user.sendReply(ErrNoSuchChannel(args[0]))
            return
        }

        channel.removeUser(user)
    }
}