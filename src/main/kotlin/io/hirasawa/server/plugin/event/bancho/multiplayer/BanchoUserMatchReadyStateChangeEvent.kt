package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.enums.MatchSlotStatus
import io.hirasawa.server.bancho.enums.MatchSlotTeam
import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a user changes their ready status in a multiplayer match
 */
class BanchoUserMatchReadyStateChangeEvent(val user: BanchoUser,
                                           val match: MultiplayerMatch,
                                           val from: MatchSlotStatus,
                                           val to: MatchSlotStatus): HirasawaEvent<BanchoUserMatchReadyStateChangeEvent>, Cancelable()