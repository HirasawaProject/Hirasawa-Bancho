package io.hirasawa.server.webserver.route

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.internalroutes.errors.RouteMethodNotAllowed
import io.hirasawa.server.webserver.internalroutes.errors.RouteNotFoundRoute
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import kotlin.collections.HashMap

class RouteContainerNode: RouteNode {
    val methods = HashMap<HttpMethod, RouteNode>()
    override fun handle(method: HttpMethod, path: List<String>, request: Request, response: Response) {
        if (method !in methods) {
            // If we have a route but don't have the method mapped present an error page
            RouteMethodNotAllowed().handle(request, response)
        } else {
            methods[method]?.handle(method, path, request, response) ?: RouteNotFoundRoute().handle(request, response)
        }

    }
}