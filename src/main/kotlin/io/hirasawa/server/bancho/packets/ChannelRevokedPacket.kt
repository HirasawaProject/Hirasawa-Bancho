package io.hirasawa.server.bancho.packets

import io.hirasawa.server.chat.ChatChannel

class ChannelRevokedPacket(channelName: String): BanchoPacket(BanchoPacketType.BANCHO_CHANNEL_REVOKED) {
    constructor(chatChannel: ChatChannel): this(chatChannel.name)
    init {
        writer.writeString(channelName)
    }
}