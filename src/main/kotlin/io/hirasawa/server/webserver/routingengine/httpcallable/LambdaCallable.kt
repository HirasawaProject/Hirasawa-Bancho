package io.hirasawa.server.webserver.routingengine.httpcallable

import io.hirasawa.server.webserver.respondable.HttpRespondable

class LambdaCallable (private val callable: () -> HttpRespondable): HttpCallable {
    override fun call(): HttpRespondable {
        return callable.invoke()
    }
}