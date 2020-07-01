package io.hirasawa.server.routes.web

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.handlers.OsuSearchBeatmapHandler
import io.hirasawa.server.plugin.HirasawaPlugin
import io.hirasawa.server.webserver.enums.ContentType
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.internalroutes.errors.RouteForbidden
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.Route

class OsuSearchRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.headers[HttpHeader.CONTENT_TYPE] = ContentType.TEXT_PLAN
        if (request.headers["user-agent"] != "osu!") {
            RouteForbidden().handle(request, response)
            return
        }

        val username = request.get["u"] ?: return
        val password = request.get["h"] ?: return
        val query = request.get["q"] ?: return
        val requestType = request.get["r"] ?: return
        val mode = request.get["m"] ?: return


        if (!Hirasawa.database.authenticate(username, password)) {
            return
        }

        val beatmaps = Hirasawa.database.getBeatmapSets(0, 100)

        response.writeText("${Hirasawa.database.getBeatmapSetAmount()}\n")
        for (beatmap in beatmaps) {
            OsuSearchBeatmapHandler(beatmap).write(response.outputStream)
        }
    }
}
