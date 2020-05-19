package io.hirasawa.server

import com.google.gson.GsonBuilder
import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.bancho.chat.ChatEngine
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.config.ChatChannelSerialiser
import io.hirasawa.server.config.HirasawaConfig
import io.hirasawa.server.database.MysqlDatabase
import io.hirasawa.server.plugin.PluginManager
import io.hirasawa.server.plugin.event.EventManager
import io.hirasawa.server.webserver.Webserver
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class Hirasawa {
    companion object {
        private val gson = GsonBuilder()
            .registerTypeAdapter(ChatChannel::class.java, ChatChannelSerialiser())
            .setPrettyPrinting()
            .create()
        val config = loadConfig()
        val webserver = Webserver(config.httpPort)
        val eventHandler = EventManager()
        val packetRouter = HashMap<BanchoPacketType, PacketHandler>()
        val pluginManager = PluginManager()
        val database = MysqlDatabase(config.database)
        val chatEngine = ChatEngine()

        val banchoUsers = HashMap<UUID, BanchoUser>()

        fun sendBanchoPacketToAll(banchoPacket: BanchoPacket) {
            for (user in banchoUsers.values) {
                user.sendPacket(banchoPacket)
            }
        }

        private fun loadConfig(): HirasawaConfig {
            if (File("config.json").exists()) {
                return gson.fromJson(FileReader(File("config.json")), HirasawaConfig::class.java)
            } else {
                val config = HirasawaConfig()
                val writer = FileWriter("config.json")
                gson.toJson(config, writer)
                writer.close()
                return config
            }
        }
    }
}