package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a user requests a skip within a multiplayer game
 */
class BanchoUserMatchSkipRequestEvent(val user: BanchoUser,
                                      val match: MultiplayerMatch): HirasawaEvent<BanchoUserMatchSkipRequestEvent>, Cancelable()