package io.hirasawa.server.webserver.routingengine.nodes

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.respondable.HttpRespondable

interface RouteNode {
    fun handle(routeSegments: ArrayList<String>, httpMethod: HttpMethod, routeParameters: HashMap<String, String>): HttpRespondable
    fun getAllMethods(routeSegments: ArrayList<String>): Array<HttpMethod>
}