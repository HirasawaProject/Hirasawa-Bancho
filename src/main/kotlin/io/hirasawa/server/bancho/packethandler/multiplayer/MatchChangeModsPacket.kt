package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.objects.Mods

class MatchChangeModsPacket: PacketHandler(BanchoPacketType.OSU_MATCH_CHANGE_MODS) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val mods = Mods.fromInt(reader.readInt())

        user.currentMatch?.setMods(user, mods)
    }
}