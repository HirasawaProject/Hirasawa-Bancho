package io.hirasawa.server.webserver.route

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class RouteTailNode(private val route: Route): RouteNode {
    override fun handle(method: HttpMethod, path: List<String>, request: Request, response: Response) {
        route.handle(request, response)
    }
}