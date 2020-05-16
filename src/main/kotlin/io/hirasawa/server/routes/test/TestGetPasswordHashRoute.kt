package io.hirasawa.server.routes.test

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import java.lang.Exception

class TestGetPasswordHashRoute: Route {
    override fun handle(request: Request, response: Response) {
        try {
            val hash = Hirasawa.database.createPasswordHash(request.get["password"]!!)
            response.writeText(hash)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
