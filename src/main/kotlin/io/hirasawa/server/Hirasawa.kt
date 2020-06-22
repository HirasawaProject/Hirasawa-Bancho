package io.hirasawa.server

import com.google.gson.GsonBuilder
import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.bancho.chat.ChatEngine
import io.hirasawa.server.bancho.objects.BanchoUserMap
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.HirasawaBot
import io.hirasawa.server.config.ChatChannelSerialiser
import io.hirasawa.server.config.HirasawaConfig
import io.hirasawa.server.database.Database
import io.hirasawa.server.database.MemoryDatabase
import io.hirasawa.server.permissions.PermissionEngine
import io.hirasawa.server.pipeline.PipelineManager
import io.hirasawa.server.plugin.PluginManager
import io.hirasawa.server.plugin.event.EventManager
import io.hirasawa.server.webserver.Webserver
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception
import java.util.*

class Hirasawa {
    companion object {
        private val gson = GsonBuilder()
            .registerTypeAdapter(ChatChannel::class.java, ChatChannelSerialiser())
            .setPrettyPrinting()
            .create()
        val config = loadConfig()
        val webserver = Webserver(config.httpPort, config.httpsPort)
        val eventHandler = EventManager()
        val packetRouter = HashMap<BanchoPacketType, PacketHandler>()
        val pluginManager = PluginManager()
        var database: Database = MemoryDatabase()
        val chatEngine = ChatEngine()
        val pipeline = PipelineManager()
        val version = Hirasawa::class.java.`package`.implementationVersion ?: "TESTING"
        lateinit var permissionEngine: PermissionEngine

        lateinit var hirasawaBot: HirasawaBot
        val banchoUsers = BanchoUserMap()

        fun sendBanchoPacketToAll(banchoPacket: BanchoPacket) {
            for (user in banchoUsers) {
                user.sendPacket(banchoPacket)
            }
        }

        fun initDatabase(database: Database) {
            this.database = database

            this.permissionEngine = PermissionEngine()

            this.hirasawaBot = HirasawaBot(database.getUser(Hirasawa.config.banchoBotId)
                ?: throw(Exception("User not found")))
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