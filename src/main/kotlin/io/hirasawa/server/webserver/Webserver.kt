package io.hirasawa.server.webserver

import io.hirasawa.server.logger.FileLogger
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.internalroutes.errors.RouteNotFoundRoute
import io.hirasawa.server.webserver.objects.MutableHeaders
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.*
import io.hirasawa.server.webserver.threads.HttpServerThread
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Webserver(val httpPort: Int) {
    // Key: host, value RouteNode "tree"-like datatype
    private val routes = HashMap<String, RouteNode>()
    private val defaultHeaders = MutableHeaders(HashMap())
    val accessLogger = FileLogger(File("logs/webserver/access.txt"))
    val errorLogger = FileLogger(File("logs/webserver/error.txt"))

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
     * @param route The instance of the route
     */
    fun addRoute(host: String, path: String, httpMethod: HttpMethod, route: Route) {
        addNode(host, path, httpMethod, RouteTailNode(route))
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
    fun addRoute(host: Any, path: String, httpMethod: HttpMethod, route: Route) {
        addRoute(host.toString(), path, httpMethod, route)
    }

    /**
     * Add a node to the route tree
     *
     * This allows custom nodes to be added that are more simple than the default features
     * @paramhost The domain the route should run under
     * @param path The url path, eg /
     * @param httpMethod The type of HTTP request, eg GET, POST
     * @param routeNode The route node to be inserted
     */
    fun addNode(host: Any, path: String, httpMethod: HttpMethod, routeNode: RouteNode) {
        val hostString = host.toString()
        if (hostString !in routes.keys) {
            routes[hostString] = DirectoryNode(RouteContainerNode(), HashMap())
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

        fun addNode(routeSegments: List<String>, currentNode: RouteNode) {
            if (routeSegments.isEmpty()) {
                if (currentNode is DirectoryNode) {
                    currentNode.index.methods[httpMethod] = routeNode
                }
            } else if (routeSegments.size == 1 && routeParameters.isNotEmpty()) {
                if (currentNode is DirectoryNode) {
                    val routeContainerNode = RouteContainerNode()
                    routeContainerNode.methods[httpMethod] = routeNode
                    currentNode.routes[routeSegments[0]] = ParameterisedRouteNode(routeParameters, routeContainerNode)
                    return
                }
            } else {
                if (currentNode is DirectoryNode) {
                    if (routeSegments[0] !in currentNode.routes) {
                        currentNode.routes[routeSegments[0]] = DirectoryNode(RouteContainerNode(), HashMap())
                    }

                    addNode(routeSegments.drop(1), currentNode.routes[routeSegments[0]]!!)
                }
            }
        }

        addNode(routeSegments, routes[hostString]!!)
    }

    /**
     * Add an asset as a route
     *
     * This allows accessing of files via the webserver, to allow access to stuff like images, HTML and the like
     * @paramhost The domain the route should run under
     * @param path The url path, eg /
     * @param httpMethod The type of HTTP request, eg GET, POST
     * @param assetLocation Where the asset exists on disk
     */
    fun addAsset(host: Any, path: String, httpMethod: HttpMethod, assetLocation: String) {
        this.addNode(host, path, httpMethod, AssetNode(assetLocation))
    }

    /**
     * Starts the webserver
     */
    fun start() {
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
        if (host in routes.keys) {
            fun search(routeNode: RouteNode, routeArray: List<String>) {
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

    /**
     * Makes a host a alias of another host and make it share the same routes
     *
     * @param from The original domain
     * @param to The aliased domain
     */
    fun cloneRoutes(from: String, to: String) {
        routes[to] = routes[from] ?: return
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

    /**
     * Removes a route from the internal webserver
     *
     * @param host The domain the route should run under
     * @param path The url path, eg /
     * @param httpMethod The type of HTTP request, eg GET, POST
     */
    fun removeRoute(host: String, path: String, httpMethod: HttpMethod) {
        removeNode(host, path, httpMethod)
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
    fun removeRoute(host: Any, path: String, httpMethod: HttpMethod) {
        removeRoute(host.toString(), path, httpMethod)
    }

    /**
     * Removes a node from the route tree
     *
     * @paramhost The domain the route should run under
     * @param path The url path, eg /
     * @param httpMethod The type of HTTP request, eg GET, POST
     */
    fun removeNode(host: Any, path: String, httpMethod: HttpMethod) {
        val hostString = host.toString()

        val routeSegments = ArrayList<String>()
        val routeParameters = ArrayList<String>()

        val pattern = Regex("\\{(.+)}")
        for (segment in path.split("/")) {
            if (segment.isBlank()) continue
            if (pattern.matches(segment)) {
                pattern.find(segment)?.groupValues?.get(1)?.let { routeParameters.add(it) }
            }
            routeSegments.add(segment)
        }

        var lastSegment = routes[hostString]
        for (segment in routeSegments) {
            if (lastSegment is DirectoryNode) {
                lastSegment = lastSegment.routes[segment]
            }
            if (lastSegment is ParameterisedRouteNode) {
                lastSegment = lastSegment.route
            }
        }

        if (lastSegment is DirectoryNode) {
            if (httpMethod in lastSegment.index.methods) {
                lastSegment.index.methods.remove(httpMethod)
            }
        }
        if (lastSegment is RouteContainerNode) {
            if (httpMethod in lastSegment.methods) {
                lastSegment.methods.remove(httpMethod)
            }
        }
    }
}