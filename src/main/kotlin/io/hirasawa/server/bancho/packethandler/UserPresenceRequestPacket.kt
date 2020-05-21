package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.handler.BanchoIntListHandler
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelAvailableAutojoinPacket
import io.hirasawa.server.bancho.packets.HandleOsuUpdatePacket
import io.hirasawa.server.bancho.packets.UserPresencePacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.bancho.ChannelLeaveEvent

class UserPresenceRequestPacket: PacketHandler(BanchoPacketType.OSU_USER_PRESENCE_REQUEST) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val intList = BanchoIntListHandler(reader).intList

        for (id in intList) {
            if (id == user.id) continue // Don't send our stats
            if (id in Hirasawa.banchoUsers) {
                val requestedUser = Hirasawa.banchoUsers[id] ?: continue
                user.sendPacket(UserPresencePacket(requestedUser))
            }
        }
    }
}