package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser

class StartSpectatingPacket: PacketHandler(BanchoPacketType.OSU_START_SPECTATING) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val spectatingId = reader.readInt()

        val spectatingUser = Hirasawa.banchoUsers[spectatingId]
        if (spectatingUser == null) {
            user.sendPrivateMessage(Hirasawa.banchoBot, "The user you are attempting to spectate is no longer online")
            return
        }

        user.spectateUser(spectatingUser)
    }
}