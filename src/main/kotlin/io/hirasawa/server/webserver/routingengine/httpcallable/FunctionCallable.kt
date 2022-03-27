package io.hirasawa.server.webserver.routingengine.httpcallable

import io.hirasawa.server.webserver.respondable.HttpRespondable
import kotlin.reflect.KFunction

class FunctionCallable (val instance: Any, val kFunction: KFunction<HttpRespondable>): HttpCallable {
    override fun call(): HttpRespondable {
        return kFunction.call(instance)
    }
}