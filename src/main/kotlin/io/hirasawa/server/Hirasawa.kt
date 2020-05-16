package io.hirasawa.server

import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.database.DatabaseCredentials
import io.hirasawa.server.database.MysqlDatabase
import io.hirasawa.server.plugin.PluginManager
import io.hirasawa.server.plugin.event.EventManager
import io.hirasawa.server.webserver.Webserver
import java.util.*

class Hirasawa {
    companion object {
        val webserver = Webserver(8080)
        val eventHandler = EventManager()
        val packetRouter = HashMap<BanchoPacketType, PacketHandler>()
        val pluginManager = PluginManager()
        val database = MysqlDatabase(DatabaseCredentials("localhost", "hirasawa", "12apples",
            "hirasawa"))

        val banchoUsers = HashMap<UUID, BanchoUser>()
    }
}