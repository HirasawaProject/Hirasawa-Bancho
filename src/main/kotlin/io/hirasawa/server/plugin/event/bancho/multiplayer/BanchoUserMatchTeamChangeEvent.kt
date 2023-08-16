package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.enums.MatchSlotTeam
import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a user changes team in a multiplayer match
 */
class BanchoUserMatchTeamChangeEvent(val user: BanchoUser,
                                     val match: MultiplayerMatch,
                                     val from: MatchSlotTeam,
                                     val to: MatchSlotTeam): HirasawaEvent<BanchoUserMatchTeamChangeEvent>, Cancelable()