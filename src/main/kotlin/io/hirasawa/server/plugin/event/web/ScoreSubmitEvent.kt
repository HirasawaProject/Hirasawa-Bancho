package io.hirasawa.server.plugin.event.web

import io.hirasawa.server.bancho.chat.message.ChatMessage
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.objects.Score
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event when player submits a score
 */
class ScoreSubmitEvent(val score: Score): HirasawaEvent, Cancelable()