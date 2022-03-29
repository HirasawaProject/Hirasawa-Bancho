package io.hirasawa.server.webserver

import io.hirasawa.server.logger.FileLogger
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.objects.MutableHeaders
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.*
import io.hirasawa.server.webserver.respondable.HttpRespondable
import io.hirasawa.server.webserver.routingengine.RoutingEngine
import io.hirasawa.server.webserver.routingengine.httpcallable.AssetCallable
import io.hirasawa.server.webserver.routingengine.httpcallable.HttpCallable
import io.hirasawa.server.webserver.routingengine.httpcallable.LambdaCallable
import io.hirasawa.server.webserver.threads.HttpServerThread
import io.hirasawa.server.webserver.threads.HttpsServerThread
import java.io.File
import kotlin.collections.HashMap
import kotlin.reflect.KFunction

class Webserver(val httpPort: Int, val httpsPort: Int) {
    // Key: host, value RouteNode "tree"-like datatype
    private val routingEngine = RoutingEngine()
    private val defaultHeaders = MutableHeaders(HashMap())
    val accessLogger = FileLogger(File("logs/webserver/access.txt"))
    val errorLogger = FileLogger(File("logs/webserver/error.txt"))

    private var sslEnabled = true

    init {
        addDefaultHeader("server", "Hirasawa")
        // This one is only temporary, in the future will automatically set the content-type depending on if it's sent
        // to a renderer or if it's just binary data
        addDefaultHeader("content-type", "text/html; charset=utf-8")
    }

    /**
     * Adds a route to the internal webserver
     *
     * You can also use parameters in the path e.g: /u/{user}
     * @param host The domain the route should run under
     * @param path The url path, eg /
     * @param httpMethod The type of HTTP request, eg GET, POST
     * @param function The function object to invoke
     */
    fun addRoute(host: Any, path: String, httpMethod: HttpMethod, function: KFunction<HttpRespondable>) {
        routingEngine["$host$path", httpMethod] = HttpCallable.fromKFunction(function)
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
    fun addRoute(host: Any, path: String, httpMethod: HttpMethod, response: () -> HttpRespondable) {
        routingEngine["$host$path", httpMethod] = LambdaCallable(response)
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
    @Deprecated("Please use the new MVC system", level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("addRoute(host, path, httpMethod, function)")
    )
    fun addRoute(host: Any, path: String, httpMethod: HttpMethod, route: Route) {
        // Hacky workaround to create a lambda that creates and returns an HttpRespondable object on the fly
        addRoute(host, path, httpMethod) {
            object: HttpRespondable() {
                override fun respond(request: Request, response: Response) {
                    route.handle(request, response)
                }
            }
        }
    }

    /**
     * Add an asset as a route
     *
     * This allows accessing of files via the webserver, to allow access to stuff like images, HTML and the like
     * @param host The domain the route should run under
     * @param path The url path, eg /
     * @param httpMethod The type of HTTP request, eg GET, POST
     * @param assetLocation Where the asset exists on disk
     */
    fun addAsset(host: Any, path: String, httpMethod: HttpMethod, assetLocation: String) {
        routingEngine["$host$path", httpMethod] = AssetCallable(assetLocation)
    }

    /**
     * Starts the webserver
     */
    fun start() {
        if (sslEnabled) {
            Thread(HttpsServerThread(httpsPort)).start()
        }
        Thread(HttpServerThread(httpPort, this)).start()
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
        routingEngine["$host$route", httpMethod].handle(request, response)
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

    /**
     * Makes a host a alias of another host and make it share the same routes
     *
     * @param from The original domain
     * @param to The aliased domain
     */
    fun cloneRoutes(from: String, to: String) {
//        routes[to] = routes[from] ?: return
    }

    /**
     * Makes a host a alias of another host and make it share the same routes
     *
     * @param from The original domain
     * @param to The aliased domain
     */
    fun cloneRoutes(from: Any, to: Any) {
        cloneRoutes(from.toString(), to.toString())
    }
}