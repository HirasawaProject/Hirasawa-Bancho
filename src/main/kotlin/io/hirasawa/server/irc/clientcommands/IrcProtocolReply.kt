package io.hirasawa.server.irc.clientcommands

import io.hirasawa.server.bancho.user.User

interface IrcProtocolReply {
    fun generate(to: User): String
}