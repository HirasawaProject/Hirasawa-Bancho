package io.hirasawa.server.irc.servercommands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.irc.clientcommands.*
import io.hirasawa.server.irc.objects.IrcUser

class JoinCommand: IrcServerCommand {
    override fun handle(user: IrcUser, command: String, args: Array<String>) {
        val channel = Hirasawa.chatEngine[user, args[0]]
        // TODO use channel join event
        if (channel == null || !channel.canUserSee(user)) {
            user.sendReply(ErrNoSuchChannel(args[0]))
            return
        }

        channel.addUser(user)
        user.sendReply(RplTopic(channel))
        user.sendReply(RplNameReply(channel, channel.connectedUsers.values.toTypedArray()))
        user.sendReply(RplNameReply(channel, arrayOf(Hirasawa.banchoBot))) // Fake BanchoBot being in every channel
        user.sendReply(RplEndOfNames(channel))
    }
}