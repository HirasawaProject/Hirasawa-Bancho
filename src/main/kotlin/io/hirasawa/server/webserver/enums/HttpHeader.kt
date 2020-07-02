package io.hirasawa.server.webserver.enums

/**
 * Enum of commonly-used HTTP header values
 */
enum class HttpHeader {
    CONTENT_TYPE,
    CONTENT_SIZE,
    USER_AGENT,
    LOCATION;

    override fun toString(): String {
        val words = this.name.split("_").map { it.toLowerCase().capitalize() }
        return words.joinToString("-")
    }
}