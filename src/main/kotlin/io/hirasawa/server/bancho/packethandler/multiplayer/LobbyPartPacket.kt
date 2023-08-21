package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelRevokedPacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.plugin.event.bancho.multiplayer.BanchoUserLobbyLeaveEvent

class LobbyPartPacket: PacketHandler(BanchoPacketType.OSU_LOBBY_JOIN) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        BanchoUserLobbyLeaveEvent(user).call()
        // For some reason this is its own packet and not just using the standard channel join packet
        // TODO make this a standard channel
//        user.sendPacket(ChannelRevokedPacket(ChatChannel("#lobby", "Place to find games I guess", false)))

        Hirasawa.multiplayer.unsubscribeToChanges(user)
    }

}