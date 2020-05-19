package io.hirasawa.server.config

import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.database.DatabaseCredentials

data class HirasawaConfig (val httpPort: Int, val httpsPort: Int, val database: DatabaseCredentials,
                           val channels: List<ChatChannel>, val banchoUserTimeout: Int) {
    constructor(): this(
        8080,
        4430,
        DatabaseCredentials(),
        arrayListOf(ChatChannel("#osu", "Main channel", true)),
        1000
    )
}