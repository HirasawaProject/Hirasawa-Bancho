package io.hirasawa.server.webserver.routingengine.nodes

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.respondable.HttpRespondable

interface RouteNode {
    fun handle(routeSegments: ArrayList<String>,
               httpMethod: HttpMethod,
               routeParameters: HashMap<String, String>,
               request: Request,
               response: Response): HttpRespondable
    fun getAllMethods(routeSegments: ArrayList<String>): Array<HttpMethod>
}