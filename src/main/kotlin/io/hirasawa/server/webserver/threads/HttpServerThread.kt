package io.hirasawa.server.webserver.threads

import io.hirasawa.server.webserver.Webserver
import java.net.ServerSocket
import java.util.concurrent.Executors

class HttpServerThread(val port: Int, private val webserver: Webserver): Runnable {
    private val threadPool = Executors.newFixedThreadPool(20)
    override fun run() {
        if (port <= 0) {
            return
        }
        val server = ServerSocket(port)

        while (true) {
            val socket = server.accept()
            threadPool.submit(HttpParserThread(socket, webserver))
        }
    }
}