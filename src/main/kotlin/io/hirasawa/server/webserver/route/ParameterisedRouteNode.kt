package io.hirasawa.server.webserver.route

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.internalroutes.errors.BadRequestRoute
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class ParameterisedRouteNode(val keys: ArrayList<String>, val route: Route): RouteNode {
    override fun handle(method: HttpMethod, path: List<String>, request: Request, response: Response) {
        if (path.size >= path.size) {
            BadRequestRoute().handle(request, response)
            return
        }

        for ((index, key) in keys.withIndex()) {
            request.routeParameters[key] = path[index]
        }

        route.handle(request, response)
    }
}