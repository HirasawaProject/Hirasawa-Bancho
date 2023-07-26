package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.multiplayer.MultiplayerManager
import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelJoinSuccessPacket
import io.hirasawa.server.bancho.packets.multiplayer.MatchUpdatePacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.HirasawaBot
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.enums.Mod
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.Mods
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class MatchInvitePacket: PacketHandler(BanchoPacketType.OSU_MATCH_INVITE) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val invitedId = reader.readInt()
        val invitedUser = Hirasawa.banchoUsers[invitedId] ?: return
        invitedUser.sendPrivateMessage(user, "Come join my multiplayer match \"[osump://${user.currentMatch?.id}/${user.currentMatch?.password} ${user.currentMatch?.gameName}]\"")
    }

}