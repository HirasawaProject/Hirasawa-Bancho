package io.hirasawa.server.controllers

import io.hirasawa.server.mvc.Controller
import io.hirasawa.server.views.HomeView
import io.hirasawa.server.webserver.respondable.HttpRespondable
import io.hirasawa.server.webserver.respondable.ViewRespondable

class HomeController: Controller {
    fun index(): HttpRespondable {
        return ViewRespondable(HomeView())
    }
}