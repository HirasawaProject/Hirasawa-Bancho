package io.hirasawa.server.routes.test

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import java.lang.Exception

class TestErrorRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.writeText("<b>This text should not appear on the error</b>")
        response.headers["ThisShouldNot"] = "Appear in the output"
        response.writeText(request.headers["SomeHeaderWeProbablyDon'tHave"]!!)
    }

}
