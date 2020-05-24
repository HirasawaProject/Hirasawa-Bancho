package io.hirasawa.server.routes.web

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.handlers.GetScoresErrorHeaderHandler
import io.hirasawa.server.handlers.GetScoresHeaderHandler
import io.hirasawa.server.handlers.ScoreInfoHandler
import io.hirasawa.server.plugin.event.score.ClientLeaderboardFailEvent
import io.hirasawa.server.plugin.event.score.ClientLeaderboardLoadEvent
import io.hirasawa.server.plugin.event.score.ClientLeaderboardPreloadEvent
import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.routes.errors.RouteForbidden

class OsuOsz2GetScoresRoute: Route {
    override fun handle(request: Request, response: Response) {
        if (request.headers[HttpHeader.USER_AGENT] != "osu!") {
            RouteForbidden().handle(request, response)
            return
        }

        val username = request.get["us"]
        val passwordHash = request.get["ha"]
        val beatmapHash = request.get["c"]
        val gamemode = GameMode.values()[request.get["m"]?.toInt() ?: 0]

        if (username == null || passwordHash == null || beatmapHash == null) {
            return
        }

        if (Hirasawa.database.authenticate(username, passwordHash)) {
            val user = Hirasawa.database.getUser(username)!!

            val preloadEvent = ClientLeaderboardPreloadEvent(user, beatmapHash, gamemode)
            Hirasawa.eventHandler.callEvent(preloadEvent)

            val beatmap = Hirasawa.database.getBeatmap(beatmapHash)
            val beatmapSet = beatmap?.beatmapSet

            if (beatmap == null || beatmapSet == null) {
                val failEvent = ClientLeaderboardFailEvent(user, beatmapHash, gamemode)
                Hirasawa.eventHandler.callEvent(failEvent)
                GetScoresErrorHeaderHandler(BeatmapStatus.NOT_SUBMITTED, false).write(response.outputStream)
                return
            }

            val loadEvent = ClientLeaderboardLoadEvent(user, beatmap, beatmapSet, gamemode)
            Hirasawa.eventHandler.callEvent(loadEvent)

            GetScoresHeaderHandler(false, beatmap, beatmapSet).write(response.outputStream)

            val userScore = Hirasawa.database.getUserScore(beatmap, gamemode, user)

            if (userScore != null) {
                ScoreInfoHandler(userScore, userScore.rank, false).write(response.outputStream)
            } else {
                response.outputStream.writeBytes("\n")
            }

            for ((index, score) in Hirasawa.database.getBeatmapScores(beatmap, gamemode, 50).withIndex()) {
                ScoreInfoHandler(score, index+1, true).write(response.outputStream)
            }
        } else {
            RouteForbidden().handle(request, response)
            return
        }
    }
}