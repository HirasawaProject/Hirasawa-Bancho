package io.hirasawa.server.bancho.packets

import io.hirasawa.server.chat.ChatChannel

class ChannelJoinSuccessPacket(chatChannel: ChatChannel): BanchoPacket(BanchoPacketType.BANCHO_CHANNEL_JOIN_SUCCESS) {
    init {
        writer.writeString(chatChannel.name)
    }
}