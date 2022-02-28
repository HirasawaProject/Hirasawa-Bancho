package io.hirasawa.server.webserver.internalroutes.errors

import io.hirasawa.server.webserver.route.Route
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class RouteMethodNotAllowed: Route {
    override fun handle(request: Request, response: Response) {
        response.httpStatus = HttpStatus.METHOD_NOT_ALLOWED

        response.writeText("<h1>405 Method Not Allowed</h1>")
        response.writeText("<p>The requested method ${request.httpMethod} is not allowed for " +
                "(${request.path}) on this server</p>")
        response.writeText("<hr>Hirasawa Project")
    }
}