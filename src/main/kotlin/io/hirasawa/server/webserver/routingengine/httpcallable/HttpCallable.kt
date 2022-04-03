package io.hirasawa.server.webserver.routingengine.httpcallable

import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.respondable.HttpRespondable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

interface HttpCallable {
    fun call(request: Request, response: Response): HttpRespondable

    companion object {
        fun fromKFunction(kFunction: KFunction<HttpRespondable>): FunctionCallable {
            // First param of KFunction is instance, we're assuming here that controllers don't have arguments
            return FunctionCallable(kFunction)
        }

        fun fromLambda(callable: (request: Request, response: Response) -> HttpRespondable): LambdaCallable {
            return LambdaCallable(callable)
        }
    }
}