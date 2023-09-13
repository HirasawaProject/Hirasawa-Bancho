package io.hirasawa.server.irc.servercommands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.irc.clientcommands.Pong
import io.hirasawa.server.irc.clientcommands.RplList
import io.hirasawa.server.irc.clientcommands.RplListEnd
import io.hirasawa.server.irc.clientcommands.RplListStart
import io.hirasawa.server.irc.objects.IrcUser

class ListCommand: IrcServerCommand {
    override fun handle(user: IrcUser, command: String, args: Array<String>) {
        user.sendReply(RplListStart())
        for (channel in Hirasawa.chatEngine.getChannels(user)) {
            user.sendReply(RplList(channel))
        }
        user.sendReply(RplListEnd())
    }
}