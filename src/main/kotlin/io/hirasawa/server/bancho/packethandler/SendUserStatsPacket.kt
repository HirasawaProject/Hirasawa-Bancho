package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.objects.BanchoStatus
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.HandleOsuUpdatePacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.bancho.BanchoUserModeChangeEvent

class SendUserStatsPacket: PacketHandler(BanchoPacketType.OSU_SEND_USER_STATS) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val status = reader.readByte()
        val statustext = reader.readString()
        val beatmapChecksum = reader.readString()
        val mods = reader.readInt()
        val mode = GameMode.values()[reader.readByte().toInt()]
        val beatmapId = reader.readInt()

        val banchoStatus = BanchoStatus(status, statustext, beatmapChecksum, mods, mode, beatmapId)

        BanchoUserModeChangeEvent(user, mode).call().then {
            user.status = banchoStatus
            user.updateUserStats(mode)
            user.sendPacket(HandleOsuUpdatePacket(user))
            for (spectator in user.spectators) {
                spectator.sendPacket(HandleOsuUpdatePacket(user))
            }
        }
    }
}