package io.hirasawa.server.routes.web

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.handlers.GetScoresHeaderHandler
import io.hirasawa.server.handlers.ScoreInfoHandler
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.objects.Score
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
            val beatmap = Beatmap(1, 1, "Test Diff", "", 2, 9.24295F)
            val beatmapSet = BeatmapSet(1, "Artist", "Title", BeatmapStatus.RANKED)
            GetScoresHeaderHandler(false, beatmap, beatmapSet).write(response.outputStream)

            val score = Score(1, Hirasawa.database.getUser("Connor"), 100, 10, 50,
                100, 300, 0, 101, 101, true, 0, 1590276979)

            val score2 = Score(2, Hirasawa.database.getUser("HirasawaBot"), 10, 10, 50,
                100, 300, 0, 101, 101, false, 0, 1590276979)

            ScoreInfoHandler(score, 1, false).write(response.outputStream)
            ScoreInfoHandler(score, 1, true).write(response.outputStream)
            ScoreInfoHandler(score2, 2, true).write(response.outputStream)
        } else {
            RouteForbidden().handle(request, response)
            return
        }
    }
}