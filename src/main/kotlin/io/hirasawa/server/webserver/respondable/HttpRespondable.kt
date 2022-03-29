package io.hirasawa.server.webserver.respondable

import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

abstract class HttpRespondable {
    val routeParameters = HashMap<String, String>()
    fun handle(request: Request, response: Response) {
        request.routeParameters.putAll(routeParameters)
        respond(request, response)
    }

    abstract fun respond(request: Request, response: Response)
}