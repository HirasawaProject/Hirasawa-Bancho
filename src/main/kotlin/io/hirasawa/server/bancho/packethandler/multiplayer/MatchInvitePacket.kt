package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser

class MatchInvitePacket: PacketHandler(BanchoPacketType.OSU_MATCH_INVITE) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val invitedId = reader.readInt()
        val invitedUser = Hirasawa.banchoUsers[invitedId] ?: return
        invitedUser.sendPrivateMessage(user, "Come join my multiplayer match \"[osump://${user.currentMatch?.id}/${user.currentMatch?.password} ${user.currentMatch?.gameName}]\"")
    }

}