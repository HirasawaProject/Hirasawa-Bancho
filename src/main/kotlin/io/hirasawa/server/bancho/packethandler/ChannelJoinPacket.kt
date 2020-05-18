package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.message.ChatMessageProvider
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelAvailableAutojoinPacket
import io.hirasawa.server.bancho.packets.ChannelJoinSuccessPacket
import io.hirasawa.server.bancho.packets.ChannelRevokedPacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.bancho.ChannelJoinEvent
import io.hirasawa.server.plugin.event.bancho.ChannelLeaveEvent

class ChannelJoinPacket: PacketHandler(BanchoPacketType.OSU_CHANNEL_JOIN) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val channelName = reader.readString()

        if (channelName in Hirasawa.chatEngine.chatChannels.keys) {
            val channel = Hirasawa.chatEngine[channelName]!!
            val event = ChannelJoinEvent(user, channel)

            Hirasawa.eventHandler.callEvent(event)

            if (event.isCancelled) {
                user.sendPacket(ChannelRevokedPacket(channelName))
            } else {
                channel.addUser(user)
                user.sendPacket(ChannelJoinSuccessPacket(channel))
            }
        } else {
            user.sendPacket(ChannelRevokedPacket(channelName))
        }
    }
}