package io.hirasawa.server.webserver.respondable

import io.hirasawa.server.mvc.View
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class ViewRespondable(private val view: View): HttpRespondable() {
    override fun respond(request: Request, response: Response) {
        response.writeText("<!DOCTYPE html>\n")
        response.writeText(view.render())
    }
}