package io.hirasawa.server.webserver.routingengine

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.respondable.HttpRespondable
import io.hirasawa.server.webserver.routingengine.httpcallable.HttpCallable
import io.hirasawa.server.webserver.routingengine.httpcallable.FunctionCallable
import io.hirasawa.server.webserver.routingengine.nodes.*

class RoutingEngine {
    private val routingTree = DomainRouteNode()

    operator fun set(path: String, method: HttpMethod = HttpMethod.GET, controllerCallable: HttpCallable) {
        val routeSegments = path.split("/")

        var lastNode: RouteNode = routingTree
        for (segment in routeSegments) {
            if (lastNode is DomainRouteNode) {
                if (segment !in lastNode) {
                    lastNode[segment] = RouteContainerNode()
                }
                lastNode = lastNode[segment]
            }
        }

        if (lastNode is RouteContainerNode) {
            val lastRouteSegment = routeSegments.last()
            if (lastRouteSegment.isEmpty()) {
                if (lastNode.index == null) {
                    lastNode.index = ControllerFunctionRouteTailNode(hashMapOf(method to controllerCallable))
                }
                if (lastNode.index is RouteTailNode) {
                    (lastNode.index as RouteTailNode)[method] = controllerCallable
                }
            } else {
                if (lastRouteSegment !in lastNode) {
                    lastNode[lastRouteSegment] = ControllerFunctionRouteTailNode(hashMapOf(method to controllerCallable))
                } else {
                    (lastNode[lastRouteSegment] as RouteTailNode)[method] = controllerCallable
                }
            }

        }
    }

    operator fun get(key: String, httpMethod: HttpMethod = HttpMethod.GET): HttpRespondable {
        val routeSegments = key.split("/") as ArrayList<String>
        routeSegments.removeAll(listOf(""))

        return routingTree.handle(routeSegments, httpMethod, HashMap())
    }
}