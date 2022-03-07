package io.hirasawa.server.irc.servercommands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.irc.clientcommands.*
import io.hirasawa.server.irc.objects.IrcUser

class JoinCommand: IrcServerCommand {
    override fun handle(user: IrcUser, command: String, args: Array<String>) {
        println(args.toList())
        val channel = Hirasawa.chatEngine[args[0]]
        if (channel == null) {
            user.sendReply(ErrNoSuchChannel(args[0]))
            return
        }

        channel.addUser(user)
        user.sendReply(RplTopic(channel))
        user.sendReply(RplNameReply(channel, channel.connectedUsers.toTypedArray()))
        user.sendReply(RplNameReply(channel, arrayOf(Hirasawa.hirasawaBot))) // Fake Hirasawa being in every channel
        user.sendReply(RplEndOfNames(channel))
    }
}