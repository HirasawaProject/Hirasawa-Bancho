package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a user changes slots in a multiplayer match
 */
class BanchoUserMatchSlotChangeEvent(val user: BanchoUser,
                                     val match: MultiplayerMatch,
                                     val from: Int,
                                     val to: Int): HirasawaEvent<BanchoUserMatchSlotChangeEvent>, Cancelable()