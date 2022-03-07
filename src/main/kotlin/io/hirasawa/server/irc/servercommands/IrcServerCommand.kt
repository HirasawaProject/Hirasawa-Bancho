package io.hirasawa.server.irc.servercommands

import io.hirasawa.server.irc.objects.IrcUser

interface IrcServerCommand {
    fun handle(user: IrcUser, command: String, args: Array<String>)
}