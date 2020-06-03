package io.hirasawa.server.routes

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.Route

class BeatmapRoute: Route {
    override fun handle(request: Request, response: Response) {
        val beatmapId = request.routeParameters["beatmap"]!!
        val mode = GameMode.values()[request.get["m"]?.toInt() ?: 0]

        val beatmap = Hirasawa.database.getBeatmap(beatmapId.toInt())

        if (beatmap == null) {
            response.writeText("<h2><b>Beatmap not found!<b></h2>")
        } else {
            val artist = beatmap.beatmapSet?.artist
            val title = beatmap.beatmapSet?.title
            response.writeText("<h2>Beatmap rankings for $artist - $title [${beatmap.difficulty}]</h2>")
            response.writeText("<a href='?m=0'>osu</a> | ")
            response.writeText("<a href='?m=1'>taiko</a> | ")
            response.writeText("<a href='?m=2'>ctb</a> | ")
            response.writeText("<a href='?m=3'>mania</a>")

            response.writeText("<table border=1>")
            response.writeText("<tr>")
            response.writeText("<th>Rank</th>")
            response.writeText("<th>User</th>")
            response.writeText("<th>Score</th>")
            response.writeText("<th>Combo</th>")
            response.writeText("<th>FC</th>")
            response.writeText("</tr>")

            for ((rank, score) in Hirasawa.database.getBeatmapScores(beatmap, mode, 50).withIndex()) {
                response.writeText("<tr>")
                response.writeText("<td>${rank+1}</td>")
                response.writeText("<td>${score.user.username}</td>")
                response.writeText("<td>${score.score}</td>")
                response.writeText("<td>${score.combo}</td>")
                response.writeText("<td>${score.fullCombo}</td>")
                response.writeText("</tr>")
            }


            response.writeText("</table>")

        }
    }
}