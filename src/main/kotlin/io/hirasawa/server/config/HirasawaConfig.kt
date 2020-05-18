package io.hirasawa.server.config

import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.database.DatabaseCredentials

data class HirasawaConfig (val httpPort: Int, val httpsPort: Int, val database: DatabaseCredentials,
                           val channels: List<ChatChannel>)