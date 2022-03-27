package io.hirasawa.server.webserver.respondable

import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

interface HttpRespondable {
    fun respond(request: Request, response: Response)
}