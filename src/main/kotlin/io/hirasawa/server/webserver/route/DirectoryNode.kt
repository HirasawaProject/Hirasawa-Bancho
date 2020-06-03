package io.hirasawa.server.webserver.route

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.internalroutes.errors.RouteNotFoundRoute
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class DirectoryNode(var index: RouteContainerNode, val routes: HashMap<String, RouteNode>): RouteNode {
    override fun handle(method: HttpMethod, path: List<String>, request: Request, response: Response) {
        if (path.isEmpty()) {
            index.handle(method, path, request, response)
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