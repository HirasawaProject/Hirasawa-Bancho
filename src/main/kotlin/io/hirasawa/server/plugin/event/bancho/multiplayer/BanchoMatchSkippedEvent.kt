package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever all users submit a skip request
 */
class BanchoMatchSkippedEvent(val match: MultiplayerMatch): HirasawaEvent<BanchoMatchSkippedEvent>