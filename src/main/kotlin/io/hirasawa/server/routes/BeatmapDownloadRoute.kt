package io.hirasawa.server.routes

import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.Route

class BeatmapDownloadRoute : Route {
    override fun handle(request: Request, response: Response) {
        response.redirect("https://bloodcat.com/osu/s/${request.routeParameters["beatmap"]}")
    }

}
