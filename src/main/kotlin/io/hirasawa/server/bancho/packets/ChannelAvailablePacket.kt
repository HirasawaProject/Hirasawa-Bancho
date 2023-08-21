package io.hirasawa.server.bancho.packets

import io.hirasawa.server.chat.ChatChannel

class ChannelAvailablePacket(chatChannel: ChatChannel): BanchoPacket(BanchoPacketType.BANCHO_CHANNEL_AVAILABLE) {
    init {
        writer.writeString(chatChannel.metadata.name)
        writer.writeString(chatChannel.metadata.description)
        writer.writeShort(chatChannel.size)
    }
}