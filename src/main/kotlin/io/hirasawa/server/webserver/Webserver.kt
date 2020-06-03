package io.hirasawa.server.webserver

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.internalroutes.errors.RouteNotFoundRoute
import io.hirasawa.server.webserver.objects.MutableHeaders
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.DirectoryNode
import io.hirasawa.server.webserver.route.ParameterisedRouteNode
import io.hirasawa.server.webserver.route.Route
import io.hirasawa.server.webserver.route.RouteNode
import io.hirasawa.server.webserver.threads.HttpServerThread
import io.hirasawa.server.webserver.threads.HttpsServerThread
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Webserver(val port: Int) {
    // Key: host, value RouteNode "tree"-like datatype
    private val routes = HashMap<String, RouteNode>()
    private val defaultHeaders = MutableHeaders(HashMap())

    private var sslEnabled = false

    init {
        addDefaultHeader("server", "Hirasawa")
        // This one is only temporary, in the future will automatically set the content-type depending on if it's sent
        // to a renderer or if it's just binary data
        addDefaultHeader("content-type", "text/html")
    }

    /**
     * Adds a route to the internal webserver
     *
     * You can also use parameters in the path e.g: /u/{user}
     * @param host The domain the route should run under
     * @param path The url path, eg /
     * @param httpMethod The type of HTTP request, eg GET, POST
     * @param route The instance of the route
     */
    fun addRoute(host: String, path: String, httpMethod: HttpMethod, route: Route) {
        if (host !in routes.keys) {
            routes[host] = DirectoryNode(null, HashMap())
        }

        val routeSegments = ArrayList<String>()
        val routeParameters = ArrayList<String>()

        val pattern = Regex("\\{(.+)}")
        for (segment in path.split("/")) {
            if (segment.isBlank()) continue
            if (pattern.matches(segment)) {
                pattern.find(segment)?.groupValues?.get(1)?.let { routeParameters.add(it) }
            } else {
                routeSegments.add(segment)
            }
        }

        fun addRoute(routeSegments: List<String>, routeNode: RouteNode) {
            if (routeSegments.isEmpty()) {
                if (routeNode is DirectoryNode) {
                    routeNode.index = route
                }
            } else {
                if (routeNode is DirectoryNode) {
                    if (routeSegments[0] !in routeNode.routes) {
                        routeNode.routes[routeSegments[0]] = DirectoryNode(null, HashMap())
                    }

                    addRoute(routeSegments.drop(1), routeNode.routes[routeSegments[0]]!!)
                }
            }
        }

        addRoute(routeSegments, routes[host]!!)
    }

    /**
     * Starts the webserver
     */
    fun start() {
        if (sslEnabled) {
            Thread(HttpsServerThread(4430)).start()
        }
        Thread(HttpServerThread(port, this)).start()
    }

    /**
     * Runs the route at the specified path and HTTP method
     *
     * In the case of errors or not being able to find a route a route describing what happened will be shown
     * Or if a route is found under GET but not under the specified HTTP method the GET will be shown instead
     *
     * @param host The domain the route is running on
     * @param route The path from the browser
     * @param httpMethod The HTTP method to check against
     */
    fun runRoute(host: String, route: String, httpMethod: HttpMethod, request: Request, response: Response) {
        if (host in routes.keys) {
            fun search(routeNode: RouteNode, routeArray: List<String>) {
                println(routeArray)
                if (routeNode is DirectoryNode) {
                    if (routeArray.isEmpty()) {
                        return routeNode.handle(httpMethod, routeArray, request, response)
                    } else {
                        val innerRoute = routeNode.routes[routeArray[0]]
                        if (innerRoute != null) {
                            search(innerRoute, routeArray.drop(1))
                        } else {
                            RouteNotFoundRoute().handle(request, response)
                        }
                    }
                } else if (routeNode is ParameterisedRouteNode) {
                    routeNode.handle(httpMethod, routeArray, request, response)
                }
            }

            val routeArray = route.split("/").filter { it.isNotEmpty() }

            search(routes[host]!!, routeArray)
        } else {
            return RouteNotFoundRoute().handle(request, response)
        }
    }

    /**
     * Get headers we normally send out with every request
     * This can be customised by calling addDefaultHeader
     *
     * @return HashMap-like object of default headers
     */
    fun getDefaultHeaders(): MutableHeaders {
        // Creates another instance of defaultHeaders
        return defaultHeaders.clone()
    }

    /**
     * Adds a header that will be send with every request
     *
     * @param key The header name
     * @param value The header value
     */
    fun addDefaultHeader(key: String, value: String) {
        defaultHeaders[key] = value
    }
}