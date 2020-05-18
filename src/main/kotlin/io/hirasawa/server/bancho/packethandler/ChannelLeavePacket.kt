package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.message.ChatMessageProvider
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelAvailableAutojoinPacket
import io.hirasawa.server.bancho.packets.ChannelJoinSuccessPacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.bancho.ChannelLeaveEvent

class ChannelLeavePacket: PacketHandler(BanchoPacketType.OSU_CHANNEL_LEAVE) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val channelName = reader.readString()

        if (channelName in Hirasawa.chatEngine.chatChannels.keys) {
            val channel = Hirasawa.chatEngine[channelName]!!
            val event = ChannelLeaveEvent(user, channel)

            Hirasawa.eventHandler.callEvent(event)

            if (event.isCancelled) {
                // Force channel to reopen
                user.sendPacket(ChannelAvailableAutojoinPacket(channel))
            } else {
                channel.removePlayer(user)
            }
        }
    }
}