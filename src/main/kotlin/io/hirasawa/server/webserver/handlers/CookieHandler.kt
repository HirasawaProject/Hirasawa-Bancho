package io.hirasawa.server.webserver.handlers

class CookieHandler(val cookieString: String) {
    var cookies =  HashMap<String, String>()

    private val equalSeparator = '='
    private val valueSeparator = ';'


    init {
        var state = ParsingState.COOKIE_NAME
        var cookieName = ""
        var cookieData = ""
        for (byte in cookieString) {
            when (state) {
                ParsingState.COOKIE_NAME -> {
                    if (byte == equalSeparator) {
                        state = ParsingState.COOKIE_DATA
                    } else {
                        cookieName += byte
                    }
                }
                ParsingState.COOKIE_DATA -> {
                    if (byte == valueSeparator) {
                        state = ParsingState.SEPARATOR
                    } else {
                        cookieData += byte
                    }
                }
                ParsingState.SEPARATOR -> {
                    if (!byte.isWhitespace()) {
                        cookies[cookieName] = cookieData

                        cookieName = ""
                        cookieData = ""
                        state = ParsingState.COOKIE_NAME
                    }
                }
            }
        }

        if (cookieName.isEmpty()) {
            cookies[cookieName] = cookieData
        }
    }


    enum class ParsingState {
        COOKIE_NAME,
        COOKIE_DATA,
        SEPARATOR,
    }
}