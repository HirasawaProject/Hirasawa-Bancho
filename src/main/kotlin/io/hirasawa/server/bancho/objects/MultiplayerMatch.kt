package io.hirasawa.server.bancho.objects

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.*
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.multiplayer.*
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.Mods
import java.util.*
import kotlin.collections.ArrayList

data class MultiplayerMatch(
    var id: Short,
    var inProgress: Boolean,
    var matchType: MatchType,
    var activeMods: Mods,
    var gameName: String,
    var password: String,
    var beatmap: Beatmap,
    var slotStatus: ArrayList<MatchSlotStatus>,
    var slotTeam: ArrayList<MatchSlotTeam>,
    var slotUser: ArrayList<BanchoUser?>,
    var hostId: Int,
    var mode: GameMode,
    var scoringType: MatchScoringType,
    var teamType: MatchTeamType,
    var specialModes: MatchSpecialMode,
    var seed: Int,
    var slotMods: ArrayList<Mods> = ArrayList(Collections.nCopies(16, Mods.fromInt(0)))) {

    constructor(gameName: String, password: String, beatmap: Beatmap, host: BanchoUser): this(
        -1,
        false,
        MatchType.POWERPLAY,
        Mods.fromInt(0),
        gameName,
        password,
        beatmap,
        ArrayList(Collections.nCopies(16, MatchSlotStatus.OPEN)),
        ArrayList(Collections.nCopies(16, MatchSlotTeam.NONE)),
        ArrayList(Collections.nCopies(16, null)),
        host.id,
        GameMode.OSU,
        MatchScoringType.SCORE,
        MatchTeamType.HEAD_TO_HEAD,
        MatchSpecialMode.NONE,
        0
    ) {
        slotStatus[0] = MatchSlotStatus.NOT_READY
        slotUser[0] = host
    }

    private val loaded = ArrayList<BanchoUser>()
    private val finished = ArrayList<BanchoUser>()
    private val skipped = ArrayList<BanchoUser>()

    val host: BanchoUser?
        get() = Hirasawa.banchoUsers[hostId]
    val size: Int
        get() {
            var size = 0

            for (status in slotStatus) {
                if (status has MatchSlotStatus.OCCUPIED) {
                    size++
                }
            }
            return size
        }
    fun isEmpty() = size == 0

    fun update(other: MultiplayerMatch) {
        if (this.teamType != other.teamType) {
            if (other.teamType in listOf(MatchTeamType.TEAM_VS, MatchTeamType.TAG_TEAM_VS)) {
                var team = MatchSlotTeam.RED
                for ((slotIndex: Int, status: MatchSlotStatus) in slotStatus.withIndex()) {
                    if (status has MatchSlotStatus.OCCUPIED) {
                        other.slotTeam[slotIndex] = team
                        team = team.toggle()
                    }
                }
            } else {
                other.slotTeam.fill(MatchSlotTeam.NONE)
            }
        }


        this.inProgress = other.inProgress
        this.matchType = other.matchType
        this.activeMods = other.activeMods
        this.gameName = other.gameName
        this.beatmap = other.beatmap
        this.slotStatus = other.slotStatus
        this.slotTeam = other.slotTeam
        this.slotUser = other.slotUser
        if (other.hostId > 0) {
            this.hostId = other.hostId
        }
        this.mode = other.mode
        this.scoringType = other.scoringType
        this.teamType = other.teamType
        this.specialModes = other.specialModes
        this.slotMods = other.slotMods
        this.seed = other.seed

        sendUpdate()
    }

    fun sendUpdate() {
        sendPacketToAll(MatchUpdatePacket(this))
        Hirasawa.multiplayer.sendUpdate(this)
    }

    fun sendPacketToAll(banchoPacket: BanchoPacket) {
        for (user in slotUser) {
            user?.sendPacket(banchoPacket)
        }
    }

    fun canJoin(): Boolean {
        return this.slotStatus.contains(MatchSlotStatus.OPEN)
    }

    fun addToMatch(user: BanchoUser): Boolean {
        if (!canJoin()) {
            return false
        }
        for ((index: Int, status: MatchSlotStatus) in slotStatus.withIndex()) {
            if (status == MatchSlotStatus.OPEN) {
                slotUser[index] = user
                slotStatus[index] = MatchSlotStatus.NOT_READY
                break
            }
        }
        user.currentMatch = this
        sendUpdate()
        return true
    }

    fun removeFromMatch(user: BanchoUser) {
        user.currentMatch = null
        for ((index: Int, scannedUser: BanchoUser?) in slotUser.withIndex()) {
            if (scannedUser == user) {
                slotUser[index] = null
                slotStatus[index] = MatchSlotStatus.OPEN
                slotTeam[index] = MatchSlotTeam.NONE
                break
            }
        }

        if (isEmpty()) {
            Hirasawa.multiplayer.removeMatch(this)
        } else if (this.host == user) {
            for (scannedUser: BanchoUser? in slotUser) {
                if (scannedUser != null) {
                    this.hostId = scannedUser.id
                }
            }
        }

        sendUpdate()
    }

    fun getUserSlot(user: BanchoUser): Int {
        for ((slot: Int, gameUser: BanchoUser?) in slotUser.withIndex()) {
            if (gameUser == user) {
                return slot
            }
        }
        return -1
    }

    fun changeSlot(slot: Int, user: BanchoUser) {
        val originalSlot = getUserSlot(user)
        if (slotStatus[slot] == MatchSlotStatus.OPEN) {
            slotUser[slot] = user
            slotStatus[slot] = slotStatus[originalSlot]
            slotTeam[slot] = slotTeam[originalSlot]

            slotUser[originalSlot] = null
            slotStatus[originalSlot] = MatchSlotStatus.OPEN
            slotTeam[originalSlot] = MatchSlotTeam.NONE
            sendUpdate()
        }
    }

    fun toggleSlot(slot: Int) {
        if (slotStatus[slot] == MatchSlotStatus.OPEN) {
            slotStatus[slot] = MatchSlotStatus.LOCKED
        } else if (slotStatus[slot] == MatchSlotStatus.LOCKED) {
            slotStatus[slot] = MatchSlotStatus.OPEN
        }

        sendUpdate()
    }

    fun setReady(banchoUser: BanchoUser, isReady: Boolean) {
        slotStatus[getUserSlot(banchoUser)] = if (isReady) {
             MatchSlotStatus.READY
        } else {
            MatchSlotStatus.NOT_READY
        }

        sendUpdate()
    }

    fun setMods(user: BanchoUser, mods: Mods) {
        var globalMods = mods
        if (specialModes has MatchSpecialMode.FREE_MOD) {
            val freeMods: Mods = mods and Hirasawa.config.multiplayerFreeMods
            globalMods -= freeMods

            slotMods[getUserSlot(user)] = mods
        }

        if (user == host) {
            setGlobalMods(globalMods)
        } else {
            // Make sure we always send an update
            sendUpdate()
        }
    }

    fun setGlobalMods(mods: Mods) {
        this.activeMods = mods

        sendUpdate()
    }

    fun setSlotStatus(user: BanchoUser, status: MatchSlotStatus) {
        slotStatus[getUserSlot(user)] = status

        sendUpdate()
    }

    fun setLoaded(user: BanchoUser) {
        loaded.add(user)
        if (loaded.size == this.size) {
            sendPacketToAll(MatchAllPlayersLoaded())
            loaded.clear()
        }
    }

    fun setFinished(user: BanchoUser) {
        finished.add(user)
        slotStatus[getUserSlot(user)] = MatchSlotStatus.NOT_READY

        sendUpdate()

        if (finished.size == this.size) {
            sendPacketToAll(MatchCompletePacket())
            finished.clear()
        }
    }

    fun setHost(host: BanchoUser) {
        this.hostId = host.id
        sendUpdate()
        host.sendPacket(MatchTransferHostPacket())
    }

    fun setHostSlot(hostSlot: Int) {
        this.setHost(slotUser[hostSlot] ?: return)
    }

    fun startGame() {
        for (multiUser: BanchoUser? in slotUser) {
            if (multiUser != null) {
                setSlotStatus(multiUser, MatchSlotStatus.PLAYING)
            }
        }

        this.sendPacketToAll(MatchStartPacket(this))
    }

    fun sendSkipRequest(user: BanchoUser) {
        skipped.add(user)
        if (skipped.size == this.size) {
            sendPacketToAll(MatchSkipPacket())
            skipped.clear()
        }
    }

    fun toggleTeam(user: BanchoUser) {
        val userSlot = getUserSlot(user)
        slotTeam[userSlot] = slotTeam[userSlot].toggle()
        sendUpdate()
    }

    fun setGamePassword(password: String) {
        this.password = password
        sendPacketToAll(MatchChangePasswordPacket(password))
    }

    fun isHost(user: BanchoUser): Boolean = user == host
    fun doesPasswordMatch(password: String): Boolean = password == this.password
    fun hasPassword(): Boolean = password.isNotBlank()
}
