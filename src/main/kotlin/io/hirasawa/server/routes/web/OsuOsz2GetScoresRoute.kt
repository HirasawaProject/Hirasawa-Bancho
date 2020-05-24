package io.hirasawa.server.routes.web

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.handlers.GetScoresErrorHeaderHandler
import io.hirasawa.server.handlers.GetScoresHeaderHandler
import io.hirasawa.server.handlers.ScoreInfoHandler
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

        if (Hirasawa.database.authenticate(request.get["us"]!!, request.get["ha"]!!)) {
            val user = Hirasawa.database.getUser(request.get["us"]!!)
            val beatmap = Hirasawa.database.getBeatmap(request.get["c"]!!)
            val beatmapSet = beatmap?.beatmapSet
            val gamemode = GameMode.values()[request.get["m"]!!.toInt()]

            if (beatmap == null || beatmapSet == null) {
                GetScoresErrorHeaderHandler(BeatmapStatus.NOT_SUBMITTED, false).write(response.outputStream)
                return
            }

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