package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.user.BanchoUser

class UserPresencePacket(banchoUser: BanchoUser): BanchoPacket(BanchoPacketType.BANCHO_USER_PRESENCE) {
    init {
        writer.writeInt(banchoUser.id)
        writer.writeString(banchoUser.username)
        writer.writeByte(banchoUser.timezone)
        writer.writeByte(banchoUser.countryCode)
        writer.writeByte(banchoUser.permissions)
        writer.writeFloat(banchoUser.longitude)
        writer.writeFloat(banchoUser.latitude)
        writer.writeInt(banchoUser.userStats.rank)
    }
}