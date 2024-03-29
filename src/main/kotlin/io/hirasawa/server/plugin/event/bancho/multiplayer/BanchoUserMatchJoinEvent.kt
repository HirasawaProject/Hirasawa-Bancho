package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.objects.Mods
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a user joins a multiplayer match
 */
class BanchoUserMatchJoinEvent(val user: BanchoUser,
                               val match: MultiplayerMatch): HirasawaEvent<BanchoUserMatchJoinEvent>, Cancelable()