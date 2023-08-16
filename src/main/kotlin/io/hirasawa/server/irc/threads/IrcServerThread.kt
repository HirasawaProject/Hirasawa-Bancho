package io.hirasawa.server.irc.threads

import java.net.ServerSocket
import java.util.concurrent.Executors

class IrcServerThread(val port: Int): Runnable {
    private val threadPool = Executors.newFixedThreadPool(10)
    override fun run() {
        if (port <= 0) {
            return
        }
        val server = ServerSocket(port)

        while (true) {
            val socket = server.accept()
            threadPool.submit(IrcParserThread(socket))
        }
    }
}