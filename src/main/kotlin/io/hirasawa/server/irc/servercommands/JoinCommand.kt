package io.hirasawa.server.irc.servercommands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.irc.clientcommands.*
import io.hirasawa.server.irc.objects.IrcUser

class JoinCommand: IrcServerCommand {
    override fun handle(user: IrcUser, command: String, args: Array<String>) {
        println(args.toList())
        val channel = Hirasawa.chatEngine[args[0]]
        if (channel == null) {
            user.sendReply(ErrNoSuchChannel(user, args[0]))
            return
        }

        channel.addUser(user)
        user.sendReply(Join(channel, user))
        user.sendReply(RplTopic(user, channel))
        user.sendReply(RplNameReply(user, channel, channel.connectedUsers.toTypedArray()))
        user.sendReply(RplNameReply(user, channel, arrayOf(Hirasawa.hirasawaBot))) // Fake Hirasawa being in every channel
        user.sendReply(RplEndOfNames(user, channel))
    }
}