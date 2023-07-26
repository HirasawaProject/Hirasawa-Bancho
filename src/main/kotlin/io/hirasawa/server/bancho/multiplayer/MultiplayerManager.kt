package io.hirasawa.server.bancho.multiplayer

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.packets.multiplayer.MatchUpdatePacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.plugin.event.bancho.multiplayer.BanchoMatchGameCreatedEvent
import io.hirasawa.server.plugin.event.bancho.multiplayer.BanchoMatchGameRemovedEvent

class MultiplayerManager {
    val matches = HashMap<Short, MultiplayerMatch>()
    val subscribedUsers = ArrayList<BanchoUser>()

    fun addMatch(match: MultiplayerMatch) {
        match.id = (matches.size + 1).toShort()
        matches[match.id] = match

        BanchoMatchGameCreatedEvent(match).call()

        for (subscribedUser: BanchoUser in subscribedUsers) {
            subscribedUser.sendPacket(MatchUpdatePacket(match))
        }
    }

    fun createMatch(name: String, beatmap: Beatmap, host: BanchoUser) {
        addMatch(MultiplayerMatch(name, "", beatmap, host))
    }

    fun removeMatch(match: MultiplayerMatch) {
        BanchoMatchGameRemovedEvent(match).call()
        matches.remove(match.id)
    }

    fun subscribeToChanges(user: BanchoUser) {
        subscribedUsers.add(user)
    }

    fun unsubscribeToChanges(user: BanchoUser) {
        subscribedUsers.remove(user)
    }

    fun sendUpdate(match: MultiplayerMatch) {
        for (subscribedUser: BanchoUser in subscribedUsers) {
            subscribedUser.sendPacket(MatchUpdatePacket(match))
        }
    }
}