package io.hirasawa.server.routes.test

import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class TestPostRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.writeText("Your POST params are ${request.post}")
    }
}