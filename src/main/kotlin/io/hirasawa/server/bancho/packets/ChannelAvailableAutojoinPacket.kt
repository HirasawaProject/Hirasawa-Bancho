package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.chat.ChatChannel

class ChannelAvailableAutojoinPacket(chatChannel: ChatChannel):
        BanchoPacket(BanchoPacketType.BANCHO_CHANNEL_AVAILABLE_AUTOJOIN) {
    init {
        writer.writeString(chatChannel.name)
        writer.writeString(chatChannel.description)
        writer.writeShort(chatChannel.size)
    }
}