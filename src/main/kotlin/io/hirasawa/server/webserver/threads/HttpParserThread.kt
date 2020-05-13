package io.hirasawa.server.webserver.threads

import io.hirasawa.server.webserver.Webserver
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.handlers.HttpHeaderHandler
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class HttpParserThread(private val socket: Socket, private val webserver: Webserver) : Runnable {
    override fun run() {
        val dataInputStream = DataInputStream(socket.getInputStream())

        val headerHandler = HttpHeaderHandler(dataInputStream)
        var postData = ByteArray(0)
        if ("Content-Length" in headerHandler.headers &&
            headerHandler.httpMethod == HttpMethod.POST) {
            // Handle POST data
            // We only do this when we're aware of content length
            val postBuffer = ByteArrayOutputStream()
            postBuffer.writeBytes(dataInputStream.readNBytes(headerHandler.headers["Content-Length"]!!.toInt()))
            postData = postBuffer.toByteArray()
        }

        val route = webserver.getRoute(headerHandler.route, headerHandler.httpMethod)
        val dataOutputStream = DataOutputStream(socket.getOutputStream())

        val responseBuffer = ByteArrayOutputStream()

        val request = Request(headerHandler.route, headerHandler.httpMethod, headerHandler.headers,
            ByteArrayInputStream(postData))
        val response = Response(HttpStatus.OK, DataOutputStream(responseBuffer), webserver.getDefaultHeaders())

        route.handle(request, response)

        // Set version and status
        dataOutputStream.writeBytes("HTTP/1.0 ")
        dataOutputStream.writeBytes(response.httpStatus.code.toString() + " ")
        dataOutputStream.writeBytes(response.httpStatus.toString())
        dataOutputStream.writeBytes("\r\n")

        for ((key, value) in response.headers) {
            dataOutputStream.writeBytes("$key: $value\r\n")
        }

        dataOutputStream.writeBytes("\r\n")

        dataOutputStream.write(responseBuffer.toByteArray())

        dataOutputStream.close()

    }
}