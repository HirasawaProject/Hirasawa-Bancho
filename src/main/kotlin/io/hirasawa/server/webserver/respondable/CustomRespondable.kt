package io.hirasawa.server.webserver.respondable

import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class CustomRespondable(private val respondable: (request: Request, response: Response) -> Unit): HttpRespondable() {
    override fun respond(request: Request, response: Response) {
        respondable.invoke(request, response)
    }
}