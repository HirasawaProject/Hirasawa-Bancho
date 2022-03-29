package io.hirasawa.server.webserver.routingengine.nodes

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.exceptions.HttpException
import io.hirasawa.server.webserver.respondable.HttpRespondable
import io.hirasawa.server.webserver.routingengine.httpcallable.HttpCallable

class ControllerFunctionRouteTailNode(private val functions: HashMap<HttpMethod, HttpCallable>): RouteTailNode {
    override fun set(key: HttpMethod, value: HttpCallable) {
        functions[key] = value
    }

    override fun handle(routeSegments: ArrayList<String>, httpMethod: HttpMethod, routeParameters: HashMap<String, String>): HttpRespondable {
        val respondable = functions[httpMethod]?.call() ?: throw HttpException(HttpStatus.METHOD_NOT_ALLOWED)
        respondable.routeParameters.putAll(routeParameters)
        return respondable
    }

    override fun getAllMethods(routeSegments: ArrayList<String>): Array<HttpMethod> {
        return functions.keys.toTypedArray()
    }
}