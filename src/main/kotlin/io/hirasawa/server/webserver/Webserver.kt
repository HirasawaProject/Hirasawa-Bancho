package io.hirasawa.server.webserver

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.objects.MutableHeaders
import io.hirasawa.server.webserver.routes.errors.RouteNotFoundRoute
import io.hirasawa.server.webserver.threads.HttpServerThread
import io.hirasawa.server.webserver.threads.HttpsServerThread
import java.util.*
import kotlin.collections.HashMap

class Webserver(val port: Int) {
    private val routes = HashMap<String, EnumMap<HttpMethod, Route>>()
    private val defaultHeaders = MutableHeaders(HashMap<String, String>())

    private var sslEnabled = false

    init {
        addDefaultHeader("server", "Hirasawa")
        // This one is only temporary, in the future will automatically set the content-type depending on if it's sent
        // to a renderer or if it's just binary data
        addDefaultHeader("content-type", "text/html")
    }

    /**
     * Adds a route to the internal webserver
     * @param path The url path, eg /
     * @param httpMethod The type of HTTP request, eg GET, POST, HEAD
     * @param route The instance of the route
     */
    fun addRoute(path: String, httpMethod: HttpMethod, route: Route) {
        if (path !in routes) {
            routes[path] = EnumMap(HttpMethod::class.java)
        }

        routes[path]!![httpMethod] = route
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
     * Gets the route at the specified path and HTTP method
     * This will ALWAYS return a valid route to display
     *
     * In the case of errors or not being able to find a route a route describing what happened will be returned
     * Or if a route is found under GET but not under the specified HTTP method the GET will be returned instead
     *
     * @param route The path from the browser
     * @param httpMethod The HTTP method to check against
     * @return A valid route that can be used to generate a response
     */
    fun getRoute(route: String, httpMethod: HttpMethod): Route {
        if (route in routes.keys) {
            // We have the route, let's see if we have the specific method
            val methods = routes[route]!!
            return if (httpMethod in methods.keys ) {
                methods[httpMethod]!!
            } else {
                if (HttpMethod.GET in methods.keys) {
                    methods[HttpMethod.GET]!!
                } else {
                    RouteNotFoundRoute()
                }
            }
        } else {
            return RouteNotFoundRoute()
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