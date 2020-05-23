package io.hirasawa.server.webserver.handlers

import io.hirasawa.server.webserver.objects.UrlSegment

class UrlSegmentHandler(url: String) {
    var urlSegment: UrlSegment

    private val routeStart = '/'
    private val paramsStart = '?'

    init {
        var host = ""
        var route = ""
        var parsingState = ParsingState.HOST
        var temp = ""
        for (char in url) {
            when(parsingState) {
                ParsingState.HOST -> {
                    if (char == routeStart) {
                        parsingState = ParsingState.ROUTE

                        // Special case since all routes need to start with '/' internally
                        route = routeStart.toString()
                    } else {
                        host += char
                    }
                }
                ParsingState.ROUTE -> {
                    if (char == paramsStart) {
                        parsingState = ParsingState.PARAMS
                    } else {
                        route += char
                    }
                }
                ParsingState.PARAMS -> {
                    temp += char
                }
            }
        }
        val params = ParameterHandler(temp.toByteArray()).parameters

        urlSegment = UrlSegment(host, route, params)
    }


    enum class ParsingState {
        HOST,
        ROUTE,
        PARAMS
    }
}