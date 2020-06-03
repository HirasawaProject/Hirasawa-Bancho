package io.hirasawa.server.webserver.route

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.internalroutes.errors.RouteNotFoundRoute
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class DirectoryNode(var index: Route?, val routes: HashMap<String, RouteNode>): RouteNode {
    override fun handle(method: HttpMethod, path: List<String>, request: Request, response: Response) {
        if (routes.size == 0) {
            if (index == null) {
                RouteNotFoundRoute().handle(request, response)
            } else {
                index?.handle(request, response)
            }
        } else {
            val routeName = path[0]
            val route = routes[routeName]
            if (route == null) {
                RouteNotFoundRoute().handle(request, response)
            } else {
                route.handle(method, path.drop(0), request, response)
            }
        }
    }
}