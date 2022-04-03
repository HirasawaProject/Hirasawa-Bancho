package io.hirasawa.server.views

import io.hirasawa.server.mvc.View
import kotlinx.html.*
import kotlinx.html.stream.createHTML

class HomeView: View {
    override fun render(): String {
        return createHTML(false).html {
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