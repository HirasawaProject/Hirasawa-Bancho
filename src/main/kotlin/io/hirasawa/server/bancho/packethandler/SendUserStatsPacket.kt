package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.objects.BanchoStatus
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelJoinSuccessPacket
import io.hirasawa.server.bancho.packets.ChannelRevokedPacket
import io.hirasawa.server.bancho.packets.HandleOsuUpdatePacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.bancho.BanchoUserModeChangeEvent
import io.hirasawa.server.plugin.event.bancho.ChannelJoinEvent

class SendUserStatsPacket: PacketHandler(BanchoPacketType.OSU_SEND_USER_STATS) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val status = reader.readByte()
        val statustext = reader.readString()
        val beatmapChecksum = reader.readString()
        val mods = reader.readInt()
        val mode = GameMode.values()[reader.readByte().toInt()]
        val beatmapId = reader.readInt()

        val banchoStatus = BanchoStatus(status, statustext, beatmapChecksum, mods, mode, beatmapId)

        val event = BanchoUserModeChangeEvent(user, mode)

        if (event.isCancelled) {
            return
        }

        user.userStats.status = banchoStatus
        user.sendPacket(HandleOsuUpdatePacket(user))
    }
}