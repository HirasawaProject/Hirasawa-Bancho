package io.hirasawa.server.webserver.enums

/**
 * Enum of commonly-used HTTP header values
 */
enum class HttpHeader {
    CONTENT_TYPE,
    CONTENT_LENGTH,
    USER_AGENT,
    LOCATION;

    override fun toString(): String {
        val words = this.name.split("_").map { it -> it.lowercase().replaceFirstChar { it.uppercase() } }
        return words.joinToString("-")
    }
}