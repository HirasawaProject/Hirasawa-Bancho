package io.hirasawa.server.webserver.routingengine.nodes

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.routingengine.httpcallable.HttpCallable

interface RouteTailNode: RouteNode {
    operator fun set(key: HttpMethod, value: HttpCallable)
}