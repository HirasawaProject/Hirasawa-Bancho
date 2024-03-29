package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a multiplayer match is removed
 */
class BanchoMatchGameRemovedEvent(val match: MultiplayerMatch): HirasawaEvent<BanchoMatchGameRemovedEvent>