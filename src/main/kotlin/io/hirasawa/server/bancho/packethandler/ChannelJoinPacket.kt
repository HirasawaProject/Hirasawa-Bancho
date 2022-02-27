package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelJoinSuccessPacket
import io.hirasawa.server.bancho.packets.ChannelRevokedPacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.bancho.ChannelJoinEvent

class ChannelJoinPacket: PacketHandler(BanchoPacketType.OSU_CHANNEL_JOIN) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val channelName = reader.readString()

        if (channelName in Hirasawa.chatEngine.chatChannels.keys) {
            val channel = Hirasawa.chatEngine[channelName]!!
            ChannelJoinEvent(user, channel).call().then {
                channel.addUser(user)
                user.sendPacket(ChannelJoinSuccessPacket(channel))
            }.cancelled {
                user.sendPacket(ChannelRevokedPacket(channelName))
            }
        } else {
            user.sendPacket(ChannelRevokedPacket(channelName))
        }
    }
}