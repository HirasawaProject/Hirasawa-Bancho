package io.hirasawa.server.webserver.routingengine.httpcallable

import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.respondable.HttpRespondable

class LambdaCallable (private val callable: (request: Request, response: Response) -> HttpRespondable): HttpCallable {
    override fun call(request: Request, response: Response): HttpRespondable {
        return callable.invoke(request, response)
    }
}