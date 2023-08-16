package io.hirasawa.server.irc.objects

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.database.tables.UsersTable
import io.hirasawa.server.irc.clientcommands.IrcProtocolReply
import org.jetbrains.exposed.sql.ResultRow
import java.util.*
import java.util.concurrent.TimeUnit

class IrcUser(id: Int, username: String, timezone: Byte, countryCode: Byte, longitude: Float, latitude: Float,
                 var uuid: UUID, isBanned: Boolean): User(id, username,
    timezone, countryCode, longitude, latitude, isBanned) {

    constructor(result: ResultRow): this(result[UsersTable.id].value, result[UsersTable.username], 0, 0, 0F ,0F,
        UUID.randomUUID(), result[UsersTable.isBanned])

    var lastKeepAlive = 0

    fun sendReply(reply: IrcProtocolReply) {
        Hirasawa.irc.sendToUser(this, reply)
    }

    /**
     * Sets the lastKeepAlive value to the current time
     * This value is used to timeout the user after inactivity
     */
    fun updateKeepAlive() {
        lastKeepAlive = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toInt()
    }
}