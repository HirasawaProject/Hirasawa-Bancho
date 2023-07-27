package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.enums.MatchSlotTeam
import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.objects.ScoreFrame
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a score frame is sent from a client during a multiplayer game
 */
class BanchoUserMatchScoreFrameEvent(val user: BanchoUser,
                                     val match: MultiplayerMatch,
                                     val scoreFrame: ScoreFrame): HirasawaEvent<BanchoUserMatchScoreFrameEvent>, Cancelable()