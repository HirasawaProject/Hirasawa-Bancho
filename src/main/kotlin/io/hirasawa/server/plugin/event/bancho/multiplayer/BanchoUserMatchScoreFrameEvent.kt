package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.enums.MatchSlotTeam
import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.objects.ScoreFrame
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a user changes slots in a multiplayer match
 */
class BanchoUserMatchScoreFrameEvent(val user: BanchoUser,
                                     val match: MultiplayerMatch,
                                     val scoreFrame: ScoreFrame): HirasawaEvent<BanchoUserMatchScoreFrameEvent>, Cancelable()