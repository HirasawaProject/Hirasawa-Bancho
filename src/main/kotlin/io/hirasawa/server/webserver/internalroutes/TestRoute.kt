package io.hirasawa.server.webserver.internalroutes

import io.hirasawa.server.webserver.route.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class TestRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.writeText("uwu")
    }
}