package io.hirasawa.server.irc.clientcommands

import io.hirasawa.server.bancho.user.User

interface IrcProtocolReply {
    fun generate(to: User): String {
        return generate(to.username)
    }
    fun generate(to: String): String
}