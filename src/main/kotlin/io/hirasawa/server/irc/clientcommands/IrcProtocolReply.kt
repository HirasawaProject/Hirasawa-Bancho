package io.hirasawa.server.irc.clientcommands

interface IrcProtocolReply {
    fun generate(): String
}