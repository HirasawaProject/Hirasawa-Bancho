package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.user.BanchoUser

class HandleOsuUpdatePacket(banchoUser: BanchoUser): BanchoPacket(BanchoPacketType.BANCHO_HANDLE_OSU_UPDATE) {
    init {
        writer.writeInt(banchoUser.id)
        writer.writeByte(banchoUser.status.status)
        writer.writeString(banchoUser.status.statusText)
        writer.writeString(banchoUser.status.beatmapChecksum)
        writer.writeInt(banchoUser.status.mods)
        writer.writeByte(banchoUser.status.mode.ordinal.toByte())
        writer.writeInt(banchoUser.status.beatmapId)
        writer.writeLong(banchoUser.userStats.rankedScore)
        // osu! expects 0 - 1 values but we store them as 0-100
        writer.writeFloat(banchoUser.userStats.accuracy / 100)
        writer.writeInt(banchoUser.userStats.playcount)
        writer.writeLong(banchoUser.userStats.totalScore)
        writer.writeInt(banchoUser.userStats.rank)
        writer.writeShort(banchoUser.userStats.pp)
    }
}