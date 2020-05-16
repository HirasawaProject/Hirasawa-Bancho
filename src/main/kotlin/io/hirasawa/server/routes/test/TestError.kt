package io.hirasawa.server.routes.test

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import java.lang.Exception

class TestError: Route {
    override fun handle(request: Request, response: Response) {
        response.writeText(request.headers["SomeHeaderWeProbablyDon'tHave"]!!)
    }

}
