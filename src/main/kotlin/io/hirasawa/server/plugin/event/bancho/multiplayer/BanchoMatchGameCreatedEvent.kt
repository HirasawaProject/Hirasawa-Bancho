package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a user changes slots in a multiplayer match
 */
class BanchoMatchGameCreatedEvent(val match: MultiplayerMatch): HirasawaEvent<BanchoMatchGameCreatedEvent>