package io.hirasawa.server.webserver.respondable

import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class BasicRespondable(private val httpStatus: HttpStatus, private val message: String): HttpRespondable {
    override fun respond(request: Request, response: Response) {
        response.httpStatus = httpStatus
        response.writeText(message)
    }
}