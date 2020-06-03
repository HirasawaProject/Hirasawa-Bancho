package io.hirasawa.server.webserver.internalroutes.errors

import io.hirasawa.server.webserver.route.Route
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class BadRequestRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.httpStatus = HttpStatus.BAD_REQUEST

        response.writeText("<h1>400 Bad Request</h1>")
        response.writeText("<p>The requested route ${request.httpMethod} (${request.path}) could not understand " +
                "your request and could not load the page</p>")
        response.writeText("<p>Please check the log for more information</p>")
        response.writeText("<hr>Hirasawa Project")
    }
}