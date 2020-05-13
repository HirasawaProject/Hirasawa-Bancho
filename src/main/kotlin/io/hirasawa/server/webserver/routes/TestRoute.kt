package io.hirasawa.server.webserver.routes

import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class TestRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.writeText("uwu")
    }
}