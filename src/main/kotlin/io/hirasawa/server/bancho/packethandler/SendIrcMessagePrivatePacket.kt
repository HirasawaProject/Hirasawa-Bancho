package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser
import java.lang.Exception

class SendIrcMessagePrivatePacket: PacketHandler(BanchoPacketType.OSU_SEND_IRC_MESSAGE_PRIVATE) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        reader.readString() // Message from, we're not gonna use this since we have the user anyway
        val message = reader.readString()
        val target = reader.readString()

        try {

            Hirasawa.chatEngine.handleChat(user, target, message)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}