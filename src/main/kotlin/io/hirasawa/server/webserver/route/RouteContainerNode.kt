package io.hirasawa.server.webserver.route

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.internalroutes.errors.RouteNotFoundRoute
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import kotlin.collections.HashMap

class RouteContainerNode: RouteNode {
    val methods = HashMap<HttpMethod, RouteNode>()
    override fun handle(method: HttpMethod, path: List<String>, request: Request, response: Response) {
        if (method !in methods) {
            if (method == HttpMethod.GET) {
                RouteNotFoundRoute().handle(request, response)
                return
            } else {
                handle(HttpMethod.GET, path, request, response)
            }
        }

        methods[method]?.handle(method, path, request, response) ?: RouteNotFoundRoute().handle(request, response)
    }
}