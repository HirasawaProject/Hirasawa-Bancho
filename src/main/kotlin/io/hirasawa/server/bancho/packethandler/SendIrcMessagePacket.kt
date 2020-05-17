package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.message.ChatMessageProvider
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser

class SendIrcMessagePacket: PacketHandler(BanchoPacketType.OSU_SEND_IRC_MESSAGE) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val messageFrom = reader.readString() // We're not gonna use this since we have the user anyway
        val message = reader.readString()
        val channel = reader.readString()

        Hirasawa.chatEngine.handleChat(user, channel, message)
    }
}