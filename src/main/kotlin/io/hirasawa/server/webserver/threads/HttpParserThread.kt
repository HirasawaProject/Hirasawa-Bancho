package io.hirasawa.server.webserver.threads

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.plugin.event.web.WebRequestEvent
import io.hirasawa.server.polyfill.readNBytes
import io.hirasawa.server.webserver.Webserver
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.exceptions.HttpException
import io.hirasawa.server.webserver.handlers.CookieHandler
import io.hirasawa.server.webserver.handlers.HttpHeaderHandler
import io.hirasawa.server.webserver.handlers.UrlSegmentHandler
import io.hirasawa.server.webserver.internalroutes.errors.*
import io.hirasawa.server.webserver.objects.Cookie
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import java.io.*
import java.net.Socket


class HttpParserThread(private val socket: Socket, private val webserver: Webserver) : Runnable {
    override fun run() {
        val dataInputStream = DataInputStream(socket.getInputStream())

        val headerHandler = HttpHeaderHandler(dataInputStream)
        val immutableHeaders = headerHandler.headers.makeImmutable()
        var postData = ByteArray(0)
        var cookiesReceived = HashMap<String, String>()
        val cookiesToSend = HashMap<String, Cookie>()
        if ("Content-Length" in immutableHeaders &&
            headerHandler.httpMethod == HttpMethod.POST) {
            // Handle POST data
            // We only do this when we're aware of content length
            val postBuffer = ByteArrayOutputStream()
            postBuffer.write(dataInputStream.readNBytes(immutableHeaders["Content-Length"]!!.toInt()))
            postData = postBuffer.toByteArray()
        }

        if ("Cookie" in immutableHeaders) {
            cookiesReceived = CookieHandler(immutableHeaders["Cookie"]!!).cookies
        }

        val urlSegment = UrlSegmentHandler(headerHandler.route).urlSegment

        val host = headerHandler.headers["host"]?.split(":")?.first() ?: ""

        val dataOutputStream = DataOutputStream(socket.getOutputStream())

        val ipAddress = socket.inetAddress.hostAddress

        val request = Request(urlSegment, headerHandler.httpMethod, immutableHeaders, cookiesReceived,
            ByteArrayInputStream(postData), ipAddress)

        val response = Response(HttpStatus.OK, dataOutputStream, webserver.getDefaultHeaders(),
            cookiesToSend)

        val webRequestEvent = WebRequestEvent(host, request, response).call()

        if (webRequestEvent.isCancelled) {
            RouteForbidden().handle(request, response)
            return
        }

        try {
            webserver.runRoute(host, urlSegment.route, headerHandler.httpMethod,
                request, response)

            if (response.isLoggingEnabled) {
                webserver.accessLogger.log("${request.ipAddress}: ${headerHandler.httpMethod} $host " +
                        urlSegment.route
                )
            }

        } catch(e: HttpException) {
            response.headers.clear()
            for ((key, value) in webserver.getDefaultHeaders()) {
                response.headers[key] = value
            }
            // This is temporary before you can register your own error pages as well as a generic error page
            // TODO Add ability to register error pages
            when (e.httpStatus) {
                HttpStatus.BAD_REQUEST -> BadRequestRoute().handle(request, response)
                HttpStatus.INTERNAL_SERVER_ERROR -> InternalServerErrorRoute().handle(request, response)
                HttpStatus.FORBIDDEN -> RouteForbidden().handle(request, response)
                HttpStatus.METHOD_NOT_ALLOWED -> RouteMethodNotAllowed().handle(request, response)
                HttpStatus.NOT_FOUND -> RouteNotFoundRoute().handle(request, response)
                else -> InternalServerErrorRoute().handle(request, response)
            }
        } catch (e: Exception) {
            val stringWriter = StringWriter()
            e.printStackTrace(PrintWriter(stringWriter))
            webserver.errorLogger.log(stringWriter)

            response.headers.clear()
            for ((key, value) in webserver.getDefaultHeaders()) {
                response.headers[key] = value
            }

            InternalServerErrorRoute().handle(request, response)
        }

        response.flush()
        response.close()
    }
}