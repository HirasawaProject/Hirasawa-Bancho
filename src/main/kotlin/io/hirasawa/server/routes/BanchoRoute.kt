package io.hirasawa.server.routes

import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.routes.errors.RouteForbidden

class BanchoRoute: Route {
    override fun handle(request: Request, response: Response) {
        if (response.headers["User-Agent"] != "osu!") {
            // Only osu! should be able to contact Bancho
            // Tell RouteForbidden to handle this request for us
            RouteForbidden().handle(request, response)
            return
        }
    }

}