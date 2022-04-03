package io.hirasawa.server.webserver.routingengine.httpcallable

import io.hirasawa.server.helpers.injectFunction
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.respondable.HttpRespondable
import kotlin.reflect.KFunction

class FunctionCallable (val kFunction: KFunction<HttpRespondable>, val instance: Any? = null): HttpCallable {
    override fun call(request: Request, response: Response): HttpRespondable {
        return injectFunction(kFunction, hashMapOf(
            "request" to request,
            "response" to response
        ))
    }
}