package io.hirasawa.server.webserver.handlers

import io.hirasawa.server.webserver.objects.UrlSegment

class UrlSegmentHandler(url: String) {
    lateinit var urlSegment: UrlSegment

    private val routeStart = '/'
    private val paramsStart = '?'

    init {
        var host = "localhost"
        var route = ""
        var params = HashMap<String, String>()
        var parsingState = ParsingState.HOST
        var temp = ""
        for (char in url) {
            when(parsingState) {
                ParsingState.HOST -> {
                    if (char == routeStart) {
                        host = temp
                        parsingState = ParsingState.ROUTE

                        // Special case since all routes need to start with '/' internally
                        temp = routeStart.toString()
                    } else {
                        temp += char
                    }
                }
                ParsingState.ROUTE -> {
                    if (char == paramsStart) {
                        route = temp
                        parsingState = ParsingState.PARAMS
                        temp = ""
                    } else {
                        temp += char
                    }
                }
                ParsingState.PARAMS -> {
                    temp += char
                }
            }
        }
        params = ParameterHandler(temp.toByteArray()).parameters

        urlSegment = UrlSegment(host, route, params)
    }


    enum class ParsingState {
        HOST,
        ROUTE,
        PARAMS
    }
}