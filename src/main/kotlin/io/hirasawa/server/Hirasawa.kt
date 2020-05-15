package io.hirasawa.server

import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.plugin.event.EventHandler
import io.hirasawa.server.plugin.event.EventManager
import io.hirasawa.server.webserver.Webserver
import java.util.HashMap

class Hirasawa {
    companion object {
        val webserver = Webserver(8080)
        val eventHandler = EventManager()
        val packetRouter = HashMap<BanchoPacketType, PacketHandler>()
    }
}