package io.hirasawa.server.bancho.handler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.*
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.Mods
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.experimental.and

class MultiplayerMatchHandler(reader: OsuReader) {
    var match: MultiplayerMatch

    init {
        val matchId = reader.readShort()
        val inProgress = reader.readBoolean()
        val matchType = MatchType.fromId(reader.readByte())
        val activeMods = Mods.fromInt(reader.readInt())
        val name = reader.readString()
        val password = reader.readString()
        val beatmapName = reader.readString()
        val beatmapId = reader.readInt()
        val beatmapHash = reader.readString()
        val slotStatus = ArrayList<MatchSlotStatus>(16)
        for (i: Int in 0..15) {
            slotStatus.add(MatchSlotStatus.fromId(reader.readByte()) ?: MatchSlotStatus.NOT_READY)
        }
        val slotTeam = ArrayList<MatchSlotTeam>(16)
        for (i: Int in 0..15) {
            slotTeam.add(MatchSlotTeam.fromId(reader.readByte()))
        }
        val slotPlayer = ArrayList<BanchoUser?>(16)
        for (i: Int in 0..15) {
            if (slotStatus[i] has MatchSlotStatus.OCCUPIED) {
                val id = reader.readInt()
                if (id == 0) {
                    slotPlayer.add(null)
                } else {
                    slotPlayer.add(Hirasawa.banchoUsers[id]!!)
                }
            } else {
                slotPlayer.add(null)
            }
        }
        val hostId = reader.readInt()
        val mode = GameMode.fromId(reader.readByte())
        val matchScoringType = MatchScoringType.fromId(reader.readByte())
        val teamType = MatchTeamType.fromId(reader.readByte())
        val specialModes = MatchSpecialMode.fromId(reader.readByte())

        // We read the slot mods but never pass them to the object, I wanna handle them myself
        val slotMods = ArrayList<Mods>(16)
        if ((specialModes.ordinal.toByte() and MatchSpecialMode.FREE_MOD.ordinal.toByte()) > 0) {
            for (i: Int in 0 .. 15) {
                slotMods.add(Mods.fromInt(reader.readInt()))
            }
        }
        val seed = reader.readInt()

        val beatmap = Beatmap(transaction {
            BeatmapsTable.select {
                BeatmapsTable.hash eq beatmapHash
            }.first()
        })


        match = MultiplayerMatch(matchId, inProgress, matchType, activeMods, name, password, beatmap,
            slotStatus, slotTeam, slotPlayer, hostId, mode, matchScoringType, teamType, specialModes, seed)
    }
}