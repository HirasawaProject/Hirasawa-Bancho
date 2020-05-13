package io.hirasawa.server

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.Webserver
import io.hirasawa.server.webserver.routes.TestRoute


fun main() {
    val webserver = Webserver(8080)

    webserver.addRoute("/", HttpMethod.GET, TestRoute())

    webserver.start()
}