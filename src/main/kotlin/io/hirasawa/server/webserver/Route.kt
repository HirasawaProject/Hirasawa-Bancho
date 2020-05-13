package io.hirasawa.server.webserver

import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

interface Route {
    fun handle(request: Request, response: Response)
}