package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelAvailableAutojoinPacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.chat.ChannelLeaveEvent

class ChannelLeavePacket: PacketHandler(BanchoPacketType.OSU_CHANNEL_LEAVE) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val channelName = reader.readString()

        val channel = Hirasawa.chatEngine[user, channelName]
        if (channel != null) {
            ChannelLeaveEvent(user, channel).call().then {
                channel.removeUser(user)
            }.cancelled {
                // Force channel to reopen
                user.sendPacket(ChannelAvailableAutojoinPacket(channel))
            }
        }
    }
}