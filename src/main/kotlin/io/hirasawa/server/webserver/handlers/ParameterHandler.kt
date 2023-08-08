package io.hirasawa.server.webserver.handlers


class ParameterHandler(parameterByteArray: ByteArray) {
    private val parameterSeparator = '&'
    private val parameterEquals = '='
    var parameters = HashMap<String, String>()

    init {
        var temp = ""
        var keyTemp = ""
        var parsingState = ParsingState.KEY

        for (newByte in parameterByteArray) {
            val newChar = Char(newByte.toInt())
            when(parsingState) {
                ParsingState.KEY -> {
                    when (newChar) {
                        parameterEquals -> {
                            keyTemp = temp
                            temp = ""
                            parsingState = ParsingState.VALUE
                        }
                        parameterSeparator -> {
                            // Assume boolean
                            parameters[temp] = "true"
                            temp = ""
                        }
                        else -> {
                            temp += newChar
                        }
                    }
                }
                ParsingState.VALUE -> {
                    when (newChar) {
                        parameterSeparator -> {
                            // Finished current parameter
                            parameters[keyTemp] = temp
                            keyTemp = ""
                            temp = ""
                            parsingState = ParsingState.KEY
                        }
                        else -> {
                            temp += newChar
                        }
                    }
                }
            }
        }

        // Do last entry
        if (keyTemp.isNotEmpty()) {
            parameters[keyTemp] = temp
        }
    }

    enum class ParsingState {
        KEY,
        VALUE
    }
}