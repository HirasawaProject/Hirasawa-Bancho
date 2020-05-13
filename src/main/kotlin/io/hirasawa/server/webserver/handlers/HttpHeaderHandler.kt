package io.hirasawa.server.webserver.handlers

import io.hirasawa.server.webserver.enums.HttpMethod
import java.io.DataInputStream

class HttpHeaderHandler(dataInputStream: DataInputStream) {
    val headers = HashMap<String, String>()
    var httpMethod = HttpMethod.GET
    var route = ""
    private var parsingState = ParsingState.HTTP_VERB

    init {
        var temp = ""
        var tempHeaderName = ""
        loop@ while (true) {
            val newChar = dataInputStream.readByte().toChar()

            if (newChar == '\r') continue

            when(parsingState) {
                ParsingState.HTTP_VERB -> {
                    if (newChar == ' ') {
                        httpMethod = HttpMethod.valueOf(temp.toUpperCase())
                        temp = ""
                        parsingState = ParsingState.HTTP_ROUTE
                    } else {
                        temp += newChar
                    }
                }
                ParsingState.HTTP_ROUTE -> {
                    if (newChar == ' ') {
                        route = temp
                        temp = ""
                        parsingState = ParsingState.HTTP_VERSION
                    } else {
                        temp += newChar
                    }
                }
                ParsingState.HTTP_VERSION -> {
                    if (newChar == '\n') {
                        temp = ""
                        parsingState = ParsingState.HEADER_NAME
                    } else {
                        temp += newChar
                    }
                }
                ParsingState.HEADER_NAME -> {
                    if (newChar == '\n') {
                        // End of headers
                        break@loop
                    } else if (newChar == ':') {
                        tempHeaderName = temp
                        temp = ""
                        parsingState = ParsingState.HEADER_VALUE
                    } else {
                        temp += newChar
                    }
                }
                ParsingState.HEADER_VALUE -> {
                    if (newChar == '\n') {
                        headers[tempHeaderName] = temp.trim()
                        temp = ""
                        parsingState = ParsingState.HEADER_NAME
                    } else {
                        temp += newChar
                    }
                }
            }
        }
    }


    enum class ParsingState {
        HTTP_VERB,
        HTTP_ROUTE,
        HTTP_VERSION,
        HEADER_NAME,
        HEADER_VALUE
    }

}