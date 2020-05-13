package io.hirasawa.server.webserver.threads

import io.hirasawa.server.webserver.Webserver
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.handlers.HttpHeaderHandler
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class HttpParserThread(private val socket: Socket, private val webserver: Webserver) : Runnable {
    override fun run() {
        val dataInputStream = DataInputStream(socket.getInputStream())

        val headerHandler = HttpHeaderHandler(dataInputStream)

        val route = webserver.getRoute(headerHandler.route, headerHandler.httpMethod)
        val dataOutputStream = DataOutputStream(socket.getOutputStream())

        val responseBuffer = ByteArrayOutputStream()

        val request = Request(headerHandler.route, headerHandler.httpMethod, headerHandler.headers)
        val response = Response(HttpStatus.OK, DataOutputStream(responseBuffer), HashMap())

        route.handle(request, response)

        // Set version and status
        dataOutputStream.writeBytes("HTTP/1.0 ")
        dataOutputStream.writeBytes(response.httpStatus.code.toString() + " ")
        dataOutputStream.writeBytes(response.httpStatus.name)
        dataOutputStream.writeBytes("\r\n")

        for ((key, value) in response.headers) {
            dataOutputStream.writeBytes("$key: $value\r\n")
        }

        dataOutputStream.writeBytes("\r\n")

        dataOutputStream.write(responseBuffer.toByteArray())

        dataOutputStream.close()

    }
}