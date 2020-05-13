package io.hirasawa.server

import io.hirasawa.server.routes.BanchoRoute
import io.hirasawa.server.routes.test.TestGetRoute
import io.hirasawa.server.routes.test.TestPostRoute
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.Webserver
import io.hirasawa.server.webserver.routes.TestRoute


fun main() {
    val webserver = Webserver(8080)

    webserver.addRoute("/", HttpMethod.GET, TestRoute())
    webserver.addRoute("/", HttpMethod.POST, BanchoRoute())

    webserver.addRoute("/test/get", HttpMethod.GET, TestGetRoute())
    webserver.addRoute("/test/post", HttpMethod.POST, TestPostRoute())

    webserver.start()
}