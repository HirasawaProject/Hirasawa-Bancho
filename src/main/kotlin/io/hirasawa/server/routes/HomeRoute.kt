package io.hirasawa.server.routes

import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.Route
import kotlinx.html.*

class HomeRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.writeRawHtml {
            head {
                title("Hirasawa Project")
            }
            body {
                h1 {
                    text("Hirasawa Project")
                }
                p {
                    text("Hello and welcome to the Hirasawa Project, please enjoy your stay, there's a lot to come")
                }
                a(href = "https://github.com/cg0/Hirasawa-Project") {
                    text("Github")
                }
                text(" | ")
                a(href = "https://twitter.com/EnglishWeeb") {
                    text("Twitter")
                }
            }
        }
    }

}
