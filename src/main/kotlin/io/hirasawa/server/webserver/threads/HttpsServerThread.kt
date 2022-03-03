package io.hirasawa.server.webserver.threads

import io.hirasawa.server.Hirasawa.Companion.webserver
import java.io.File
import java.util.concurrent.Executors
import javax.net.ssl.SSLServerSocketFactory


class HttpsServerThread(val port: Int): Runnable {
    private val threadPool = Executors.newFixedThreadPool(10)
    override fun run() {
        if (port <= 0) {
            return
        }

        if (!File("keystore.pfx").exists()) {
            println("You are missing the keystore.pfx file, this is required for HTTPS connections.")
            println("Falling back to HTTP only mode!")
            println("Bare in mind osu! requires an HTTPS connection so clients will not be able to connect")
            return
        }

        System.setProperty("javax.net.ssl.keyStore", "./keystore.pfx")
        System.setProperty("javax.net.ssl.keyStorePassword", "password")


        val ssf = SSLServerSocketFactory.getDefault() as SSLServerSocketFactory
        val server = ssf.createServerSocket(port)

        while (true) {
            val socket = server.accept()
            threadPool.submit(HttpParserThread(socket, webserver))
        }
    }

}