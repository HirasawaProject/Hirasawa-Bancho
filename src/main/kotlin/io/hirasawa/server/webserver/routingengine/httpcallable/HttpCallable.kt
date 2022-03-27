package io.hirasawa.server.webserver.routingengine.httpcallable

import io.hirasawa.server.webserver.respondable.HttpRespondable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

interface HttpCallable {
    fun call(): HttpRespondable

    companion object {
        fun fromKFunction(kFunction: KFunction<HttpRespondable>): FunctionCallable {
            // First param of KFunction is instance, we're assuming here that controllers don't have arguments
            // TODO dependency injection
            // TODO cache class instance (probably with global caching system)
            val instanceKClass = kFunction.parameters.first().type.classifier as KClass<*>
            val instance = instanceKClass.constructors.first().call()

            return FunctionCallable(instance, kFunction)
        }

        fun fromLambda(callable: () -> HttpRespondable): LambdaCallable {
            return LambdaCallable(callable)
        }
    }
}