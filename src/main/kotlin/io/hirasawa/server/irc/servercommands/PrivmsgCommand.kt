package io.hirasawa.server.irc.servercommands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.irc.clientcommands.Pong
import io.hirasawa.server.irc.clientcommands.RplList
import io.hirasawa.server.irc.clientcommands.RplListEnd
import io.hirasawa.server.irc.clientcommands.RplListStart
import io.hirasawa.server.irc.objects.IrcUser

class PrivmsgCommand: IrcServerCommand {
    override fun handle(user: IrcUser, command: String, args: Array<String>) {
        val message = args.sliceArray(IntRange(1, args.size - 1)).joinToString(" ").removePrefix(":")
        println("PRIVMSG cmd: ${args[0]} $message\n")
        Hirasawa.chatEngine.handleChat(user, args[0], message)
    }
}