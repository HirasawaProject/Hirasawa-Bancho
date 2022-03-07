package io.hirasawa.server.irc.objects

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.message.PrivateChatMessage
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.database.tables.UsersTable
import io.hirasawa.server.irc.clientcommands.IrcProtocolReply
import io.hirasawa.server.irc.clientcommands.Privmsg
import org.jetbrains.exposed.sql.ResultRow
import java.util.*

class IrcUser(id: Int, username: String, timezone: Byte, countryCode: Byte, longitude: Float, latitude: Float,
                 var uuid: UUID, isBanned: Boolean): User(id, username,
    timezone, countryCode, longitude, latitude, isBanned) {

    constructor(result: ResultRow): this(result[UsersTable.id].value, result[UsersTable.username], 0, 0, 0F ,0F,
        UUID.randomUUID(), result[UsersTable.banned])

    fun sendReply(reply: IrcProtocolReply) {
        Hirasawa.irc.sendToUser(this, reply)
    }
}