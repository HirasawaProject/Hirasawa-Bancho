package io.hirasawa.server.webserver.routingengine.nodes

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.exceptions.HttpException
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.respondable.HttpRespondable

open class RouteContainerNode: RouteNode {
    private val container = HashMap<String, RouteNode>()
    private val wildcardRoute = "*"
    private var wildcardKey = ""
    var index: RouteTailNode? = null

    operator fun get(key: String): RouteNode {
        return if (key.isEmpty()) {
            index ?: throw HttpException(HttpStatus.NOT_FOUND)
        } else if (key.startsWith('{') && key.endsWith('}')) {
            container[wildcardRoute] ?: throw HttpException(HttpStatus.NOT_FOUND)
        } else if (key in container){
            container[key.lowercase()] ?: throw HttpException(HttpStatus.NOT_FOUND)
        } else if (wildcardRoute in container) {
            container[wildcardRoute] ?: throw HttpException(HttpStatus.NOT_FOUND)
        } else {
            throw HttpException(HttpStatus.NOT_FOUND)
        }
    }

    operator fun set(key: String, value: RouteNode) {
        if (key.isEmpty()) {
            if (value is RouteTailNode) {
                index = value
            }
        } else if (key.startsWith('{') && key.endsWith('}')) {
            wildcardKey = key.removeSurrounding("{", "}")
            container[wildcardRoute] = value
        } else {
            container[key.lowercase()] = value
        }
    }

    operator fun contains(key: String): Boolean {
        return key.lowercase() in container
    }

    override fun handle(routeSegments: ArrayList<String>,
                        httpMethod: HttpMethod,
                        routeParameters: HashMap<String, String>,
                        request: Request,
                        response: Response): HttpRespondable {
        if (routeSegments.isEmpty()) {
            return index?.handle(routeSegments, httpMethod, routeParameters, request, response) ?: throw HttpException(HttpStatus.NOT_FOUND)
        }
        val routeSegment = routeSegments.first()
        if (this.contains(routeSegment)) {
            routeSegments.removeFirst()
            return this[routeSegment].handle(routeSegments, httpMethod, routeParameters, request, response)
        } else if (this.contains(wildcardRoute)) {
            routeSegments.removeFirst()
            request.routeParameters[wildcardKey] = routeSegment
            return this[routeSegment].handle(routeSegments, httpMethod, routeParameters, request, response)
        }
        throw HttpException(HttpStatus.NOT_FOUND)
    }

    override fun getAllMethods(routeSegments: ArrayList<String>): Array<HttpMethod> {
        if (routeSegments.isEmpty()) {
            return index?.getAllMethods(routeSegments) ?: throw HttpException(HttpStatus.NOT_FOUND)
        }
        val routeSegment = routeSegments.first()
        if (this.contains(routeSegment)) {
            routeSegments.removeFirst()
            return this[routeSegment].getAllMethods(routeSegments)
        }
        throw HttpException(HttpStatus.NOT_FOUND)
    }
}