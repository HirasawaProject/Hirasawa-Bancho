package io.hirasawa.server.webserver.route

import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

@Deprecated("Please use the new MVC system", level = DeprecationLevel.WARNING)
interface Route {
    fun handle(request: Request, response: Response)
}