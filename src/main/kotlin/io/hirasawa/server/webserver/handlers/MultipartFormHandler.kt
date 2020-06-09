package io.hirasawa.server.webserver.handlers

import io.hirasawa.server.webserver.objects.MutableHeaders


class MultipartFormHandler(byteArray: ByteArray, boundary: String) {
    private val newLine = '\n'
    private val headerSeparator = ':'
    val elements = HashMap<String, String>()
    val files = HashMap<String, ByteArray>()

    init {
        var parsingState = ParsingState.BOUNDARY
        var headerKey = ""
        var headerValue = ""
        val headers = MutableHeaders(HashMap())
        val elementContent = ArrayList<Byte>()
        val elementLine = ArrayList<Byte>()

        for (byte in byteArray) {
            val newChar = byte.toChar()

            when (parsingState) {
                ParsingState.BOUNDARY -> {
                    // First boundary can just be skipped at a \n
                    if (newChar == newLine) {
                        parsingState = ParsingState.HEADER_KEY
                    }
                }
                ParsingState.HEADER_KEY -> {
                    if (newChar == headerSeparator) {
                        parsingState = ParsingState.HEADER_VALUE
                    } else if (newChar == newLine) {
                        parsingState = ParsingState.CONTENT
                    } else {
                        headerKey += newChar
                    }
                }
                ParsingState.HEADER_VALUE -> {
                    if (newChar == newLine) {
                        headers[headerKey] = headerValue.trim()
                        headerKey = ""
                        headerValue = ""
                        parsingState = ParsingState.HEADER_KEY
                    } else {
                        headerValue += newChar
                    }
                }
                ParsingState.CONTENT -> {
                    elementLine.add(byte)
                    if (newChar == newLine) {
                        val stringContent = String(elementLine.toByteArray())
                        if (boundary in stringContent) {
                            // Process end of content

                            // Extra data from headers, there's probably a better way
                            var elementName = ""
                            var isFile = false

                            val regex = Regex(".+=\"(.+)\"")
                            for ((key, value) in headers) {
                                when (key) {
                                    "content-disposition" -> {
                                        val data = value.split("; ")
                                        elementName = regex.matchEntire(data[1])?.groupValues?.get(1) ?: ""
                                    }
                                    "content-type" -> {
                                        isFile = !value.contains("text")
                                    }
                                }
                            }

                            if (isFile) {
                                files[elementName] = elementContent.dropLast(2).toByteArray()
                            } else {
                                elements[elementName] = String(elementContent.dropLast(2).toByteArray())
                            }

                            // clear data
                            headerKey = ""
                            headerValue = ""
                            elementContent.clear()

                            parsingState = ParsingState.HEADER_KEY
                        } else {
                            elementContent.addAll(elementLine)
                        }
                        elementLine.clear()
                    }
                }
            }

        }

    }

    private enum class ParsingState {
        BOUNDARY,
        HEADER_KEY,
        HEADER_VALUE,
        CONTENT
    }
}