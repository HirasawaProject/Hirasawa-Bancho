package io.hirasawa.server.webserver.routingengine.nodes

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.exceptions.HttpException
import io.hirasawa.server.webserver.respondable.HttpRespondable

open class RouteContainerNode: RouteNode {
    private val container = HashMap<String, RouteNode>()
    private val wildcardRoute = "*"
    private var wildcardKey = ""
    var index: RouteTailNode? = null

    operator fun get(key: String): RouteNode {
        return container[key.lowercase()] ?: throw HttpException(HttpStatus.NOT_FOUND)
    }

    operator fun set(key: String, value: RouteNode) {
        if (key.startsWith('{') && key.endsWith('}')) {
            wildcardKey = key.removeSurrounding("{", "}")
            container[wildcardRoute] = value
        } else {
            container[key.lowercase()] = value
        }
    }

    operator fun contains(key: String): Boolean {
        return key.lowercase() in container
    }

    override fun handle(routeSegments: ArrayList<String>, httpMethod: HttpMethod, routeParameters: HashMap<String, String>): HttpRespondable {
        if (routeSegments.isEmpty()) {
            return index?.handle(routeSegments, httpMethod, routeParameters) ?: throw HttpException(HttpStatus.NOT_FOUND)
        }
        val routeSegment = routeSegments.first()
        if (this.contains(routeSegment)) {
            routeSegments.removeFirst()
            return this[routeSegment].handle(routeSegments, httpMethod, routeParameters)
        } else if (this.contains(wildcardRoute)) {
            routeSegments.removeFirst()
            routeParameters[wildcardKey] = routeSegment
            return this[routeSegment].handle(routeSegments, httpMethod, routeParameters)
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