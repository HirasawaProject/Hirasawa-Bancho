package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelJoinSuccessPacket
import io.hirasawa.server.bancho.packets.multiplayer.MatchUpdatePacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.chat.ChatChannel

class LobbyJoinPacket: PacketHandler(BanchoPacketType.OSU_LOBBY_JOIN) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        // For some reason this is its own packet and not just using the standard channel join packet
        user.sendPacket(ChannelJoinSuccessPacket(ChatChannel("#lobby", "Place to find games I guess", false)))

        Hirasawa.multiplayer.subscribeToChanges(user)
        for (match in Hirasawa.multiplayer.matches) {
            user.sendPacket(MatchUpdatePacket(match.value))
        }
    }

}