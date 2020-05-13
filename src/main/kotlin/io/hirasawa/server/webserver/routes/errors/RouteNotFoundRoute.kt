package io.hirasawa.server.webserver.routes.errors

import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class RouteNotFoundRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.httpStatus = HttpStatus.NOT_FOUND

        response.writeText("<h1>404 Not Found</h1>")
        response.writeText("<p>The requested route ${request.httpMethod} (${request.path}) was not found on this " +
                "server</p>")
        response.writeText("<hr>Hirasawa Project")
    }

}