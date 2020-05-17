package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.user.BanchoUser

class HandleOsuUpdatePacket(banchoUser: BanchoUser): BanchoPacket(BanchoPacketType.BANCHO_HANDLE_OSU_UPDATE) {
    init {
        writer.writeInt(banchoUser.id)
        writer.writeByte(banchoUser.userStats.status.status)
        writer.writeString(banchoUser.userStats.status.statusText)
        writer.writeString(banchoUser.userStats.status.beatmapChecksum)
        writer.writeInt(banchoUser.userStats.status.mods)
        writer.writeByte(banchoUser.userStats.status.mode.ordinal.toByte())
        writer.writeInt(banchoUser.userStats.status.beatmapId)
        writer.writeLong(banchoUser.userStats.rankedScore)
        writer.writeFloat(banchoUser.userStats.accuracy)
        writer.writeInt(banchoUser.userStats.playcount)
        writer.writeLong(banchoUser.userStats.totalScore)
        writer.writeInt(banchoUser.userStats.rank)
        writer.writeShort(banchoUser.userStats.pp)
    }
}