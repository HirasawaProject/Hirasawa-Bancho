package io.hirasawa.server.mvc

import io.hirasawa.server.webserver.respondable.ViewRespondable

interface Controller {
    fun view(view: View): ViewRespondable {
        return ViewRespondable((view))
    }
}