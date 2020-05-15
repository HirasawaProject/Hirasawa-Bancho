package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser

abstract class PacketHandler(val packetType: BanchoPacketType) {
    abstract fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser)
}
