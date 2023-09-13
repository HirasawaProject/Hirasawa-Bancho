package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelJoinSuccessPacket
import io.hirasawa.server.bancho.packets.ChannelRevokedPacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.chat.ChannelJoinEvent

class ChannelJoinPacket: PacketHandler(BanchoPacketType.OSU_CHANNEL_JOIN) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val channelName = reader.readString()

        val channel = Hirasawa.chatEngine[user, channelName]
        if (channel == null || !channel.canUserSee(user)) {
            user.sendPacket(ChannelRevokedPacket(channelName))
        } else {
            if (user in channel.connectedUsers) {
                // User is already in channel, we'll just say it was successful to connect
                user.sendPacket(ChannelJoinSuccessPacket(channel))
                return
            }
            ChannelJoinEvent(user, channel).call().then {
                channel.addUser(user)
                user.sendPacket(ChannelJoinSuccessPacket(channel))
            }.cancelled {
                user.sendPacket(ChannelRevokedPacket(channelName))
            }
        }
    }
}