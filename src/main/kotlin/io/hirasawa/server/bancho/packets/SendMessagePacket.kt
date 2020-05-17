package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.chat.message.ChatMessage

class SendMessagePacket(chatMessage: ChatMessage): BanchoPacket(BanchoPacketType.BANCHO_SEND_MESSAGE) {
    init {
        writer.writeString(chatMessage.source.username)
        writer.writeString(chatMessage.message)
        writer.writeString(chatMessage.destinationName)
        writer.writeInt(chatMessage.source.id)
    }
}