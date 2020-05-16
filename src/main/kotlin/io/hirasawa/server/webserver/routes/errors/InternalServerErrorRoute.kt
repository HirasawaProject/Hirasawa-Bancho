package io.hirasawa.server.webserver.routes.errors

import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class InternalServerErrorRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR

        response.writeText("<h1>500 Internal Server Error</h1>")
        response.writeText("<p>The requested route ${request.httpMethod} (${request.path}) encountered an error " +
                "while loading the page</p>")
        response.writeText("<p>Please check the log for more information</p>")
        response.writeText("<hr>Hirasawa Project")
    }
}