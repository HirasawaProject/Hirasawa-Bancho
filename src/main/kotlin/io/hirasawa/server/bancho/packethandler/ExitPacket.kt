package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.QuitReason
import io.hirasawa.server.bancho.enums.QuitState
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.HandleUserQuitPacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.chat.UserQuitEvent

class ExitPacket: PacketHandler(BanchoPacketType.OSU_EXIT) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val quitReason = QuitReason.values()[reader.readInt()]

        UserQuitEvent(user, quitReason).call().then {
            Hirasawa.sendBanchoPacketToAll(HandleUserQuitPacket(user, QuitState.GONE))
            Hirasawa.banchoUsers.remove(user)
        }
    }
}