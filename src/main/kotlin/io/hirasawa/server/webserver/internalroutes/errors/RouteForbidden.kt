package io.hirasawa.server.webserver.internalroutes.errors

import io.hirasawa.server.webserver.route.Route
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class RouteForbidden: Route {
    override fun handle(request: Request, response: Response) {
        response.httpStatus = HttpStatus.FORBIDDEN

        response.writeText("<h1>403 Forbidden</h1>")
        response.writeText("<p>You are forbidden from accessing the requested route ${request.httpMethod} " +
                "(${request.path}) on this server</p>")
        response.writeText("<hr>Hirasawa Project")
    }
}