package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.bancho.handler.MultiplayerMatchHandler
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser

class MatchChangeSettingPacket: PacketHandler(BanchoPacketType.OSU_MATCH_CHANGE_SETTINGS) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val match = MultiplayerMatchHandler(reader).match

        if (user.currentMatch?.isHost(user) ?: return) {
            user.currentMatch?.update(match)
        }
    }

}