package io.hirasawa.server.bancho.packets

class ChannelAvailablePacket(channelName: String): BanchoPacket(BanchoPacketType.BANCHO_CHANNEL_AVAILABLE) {
    init {
        writer.writeString(channelName)
    }
}