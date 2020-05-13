package io.hirasawa.server.routes.test

import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class TestGetRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.writeText("Your GET params are ${request.get}")
    }
}