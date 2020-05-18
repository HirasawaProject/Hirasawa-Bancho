package io.hirasawa.server

import com.google.gson.Gson
import io.hirasawa.server.bancho.chat.ChatEngine
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.database.DatabaseCredentials
import io.hirasawa.server.database.MysqlDatabase
import io.hirasawa.server.plugin.PluginManager
import io.hirasawa.server.plugin.event.EventManager
import io.hirasawa.server.webserver.Webserver
import java.io.File
import java.io.FileReader
import java.util.*

class Hirasawa {
    companion object {
        val config = Gson().fromJson(FileReader(File("config.json")), HirasawaConfig::class.java)
        val webserver = Webserver(config.httpPort)
        val eventHandler = EventManager()
        val packetRouter = HashMap<BanchoPacketType, PacketHandler>()
        val pluginManager = PluginManager()
        val database = MysqlDatabase(config.databaseCredentials)
        val chatEngine = ChatEngine()

        val banchoUsers = HashMap<UUID, BanchoUser>()

        fun sendBanchoPacketToAll(banchoPacket: BanchoPacket) {
            for (user in banchoUsers.values) {
                user.sendPacket(banchoPacket)
            }
        }
    }
}