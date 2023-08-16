package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a match game is loaded by all players
 */
class BanchoMatchGameLoadedEvent(val match: MultiplayerMatch): HirasawaEvent<BanchoMatchGameLoadedEvent>