package io.hirasawa.server.irc.clientcommands

import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.irc.enums.NumericReply

open class NumericIrcReply(
    private val from: String,
    private val numericReply: NumericReply,
    private val args: String
): IrcProtocolReply {
    override fun generate(to: User): String {
        println(":$from ${numericReply.id} ${to.username} $args\r\n")
        return ":$from ${numericReply.id} ${to.username} $args\r\n"
    }

}