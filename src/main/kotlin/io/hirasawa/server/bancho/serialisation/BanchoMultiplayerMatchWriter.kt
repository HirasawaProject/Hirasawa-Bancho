package io.hirasawa.server.bancho.serialisation

import io.hirasawa.server.bancho.enums.MatchSlotTeam
import io.hirasawa.server.bancho.enums.MatchSlotStatus
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.objects.Mods

class BanchoMultiplayerMatchWriter(private val match: MultiplayerMatch): SerialisedBanchoObject {
    override fun write(osuWriter: OsuWriter) {
        osuWriter.writeShort(match.id)
        osuWriter.writeBoolean(match.inProgress)
        osuWriter.writeByte(match.matchType.ordinal.toByte())
        osuWriter.writeInt(match.activeMods.toInt())
        osuWriter.writeString(match.gameName)
        // We don't send the password to the client as it doesn't need it, we verify joins and handle match invites
        // osu! however does require a value to be given to understand if the match is locked or not so we just send
        // the string "PROTECTED"
        osuWriter.writeString(if(match.hasPassword()) "PROTECTED" else "")
        osuWriter.writeString(match.beatmap.beatmapSet.title)
        osuWriter.writeInt(match.beatmap.osuId)
        osuWriter.writeString(match.beatmap.hash)
        for (status: MatchSlotStatus in match.slotStatus) {
            osuWriter.writeByte(status.id)
        }
        for (team: MatchSlotTeam in match.slotTeam) {
            osuWriter.writeByte(team.ordinal.toByte())
        }
        for ((slot: Int, user: BanchoUser?) in match.slotUser.withIndex()) {
            if (match.slotStatus[slot] has MatchSlotStatus.OCCUPIED) {
                if (user != null) {
                    osuWriter.writeInt(user.id)
                }
            }
        }
        osuWriter.writeInt(match.hostId)
        osuWriter.writeByte(match.mode.ordinal.toByte())
        osuWriter.writeByte(match.scoringType.ordinal.toByte())
        osuWriter.writeByte(match.teamType.ordinal.toByte())
        osuWriter.writeByte(match.specialModes.ordinal.toByte())
        for (slotMod: Mods in match.slotMods) {
            osuWriter.writeInt(slotMod.toInt())
        }
        osuWriter.writeInt(match.seed)
    }
}