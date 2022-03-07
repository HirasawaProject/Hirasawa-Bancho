package io.hirasawa.server.irc.clientcommands

import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.irc.enums.TextualReply

open class TextualIrcReply(
    private val from: String,
    private val textualReply: TextualReply,
    private val args: String
): IrcProtocolReply {
    override fun generate(to: User): String {
        println(":$from $textualReply $args\r\n")
        return ":$from $textualReply $args\r\n"
    }

}