package io.hirasawa.server.webserver.routingengine

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.respondable.HttpRespondable
import io.hirasawa.server.webserver.routingengine.httpcallable.HttpCallable
import io.hirasawa.server.webserver.routingengine.nodes.*

class RoutingEngine {
    private val routingTree = DomainRouteNode()

    operator fun set(path: String, method: HttpMethod = HttpMethod.GET, controllerCallable: HttpCallable) {
        val routeSegments = path.split("/")

        var lastNode: RouteNode = routingTree
        for (segment in routeSegments) {
            if (lastNode is RouteContainerNode) {
                if (segment.isEmpty()) break
                if (segment !in lastNode) {
                    lastNode[segment] = RouteContainerNode()
                }
                lastNode = lastNode[segment]
            }
        }

        if (lastNode is RouteContainerNode) {
            if (lastNode.index == null) {
                lastNode.index = ControllerFunctionRouteTailNode(hashMapOf(method to controllerCallable))
            }
            if (lastNode.index is RouteTailNode) {
                (lastNode.index as RouteTailNode)[method] = controllerCallable
            }

        }
    }

    operator fun get(key: String, httpMethod: HttpMethod = HttpMethod.GET, request: Request, response: Response): HttpRespondable {
        val routeSegments = key.split("/") as ArrayList<String>
        routeSegments.removeAll(listOf(""))

        return routingTree.handle(routeSegments, httpMethod, HashMap(), request, response)
    }

    fun cloneDomain(from: Any, to: Any) {
        routingTree[to.toString()] = routingTree[from.toString()]
    }
}