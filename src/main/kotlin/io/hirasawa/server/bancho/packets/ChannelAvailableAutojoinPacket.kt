package io.hirasawa.server.bancho.packets

import io.hirasawa.server.chat.ChatChannel

class ChannelAvailableAutojoinPacket(chatChannel: ChatChannel):
        BanchoPacket(BanchoPacketType.BANCHO_CHANNEL_AVAILABLE_AUTOJOIN) {
    init {
        writer.writeString(chatChannel.metadata.name)
        writer.writeString(chatChannel.metadata.description)
        writer.writeShort(chatChannel.size)
    }
}