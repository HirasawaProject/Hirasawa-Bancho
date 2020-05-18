package io.hirasawa.server.bancho.handler

import java.io.ByteArrayInputStream
import java.io.DataInputStream

class BanchoLoginHandler(dataInputStream: ByteArrayInputStream) {
    var username = ""
    var password = ""
    var buildName = ""
    var utcOffset: Byte = 0
    var showCityLocation = false
    var clientHash = ""
    var blockNonFriendPm = false

    private val newLine = '\n'
    private val segments = '|'

    init {
        var parsingState = ParsingState.USERNAME
        var temp = ""
        while(dataInputStream.available() > 0) {
            val newChar = dataInputStream.read().toChar()
            when (parsingState) {
                ParsingState.USERNAME -> {
                    if (newChar == newLine) {
                        parsingState = ParsingState.PASSWORD
                    } else {
                        username += newChar
                    }
                }
                ParsingState.PASSWORD -> {
                    if (newChar == newLine) {
                        parsingState = ParsingState.BUILD_NAME
                    } else {
                        password += newChar
                    }
                }
                ParsingState.BUILD_NAME -> {
                    if (newChar == segments) {
                        parsingState = ParsingState.UTC_OFFSET
                    } else {
                        buildName += newChar
                    }
                }
                ParsingState.UTC_OFFSET -> {
                    if (newChar == segments) {
                        utcOffset = temp.toByte()
                        parsingState = ParsingState.CITY_LOCATION
                    } else {
                        temp += newChar
                    }
                }
                ParsingState.CITY_LOCATION -> {
                    if (newChar == segments) {
                        parsingState = ParsingState.CLIENT_HASH
                    } else {
                        showCityLocation = newChar == '1'
                    }

                }
                ParsingState.CLIENT_HASH -> {
                    if (newChar == segments) {
                        parsingState = ParsingState.BLOCK_PM
                    } else {
                        clientHash += newChar
                    }
                }
                ParsingState.BLOCK_PM -> {
                    blockNonFriendPm = newChar == '1'
                }
            }
        }

    }

    enum class ParsingState {
        USERNAME,
        PASSWORD,
        BUILD_NAME,
        UTC_OFFSET,
        CITY_LOCATION,
        CLIENT_HASH,
        BLOCK_PM
    }
}