package io.hirasawa.server.webserver.objects

import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.enums.HttpStatus

class MutableHeaders(private val headers: HashMap<String, String>): ImmutableHeaders(headers) {
    operator fun set(key: String, value: String) {
        this.headers[key.toLowerCase()] = value
    }

    operator fun set(key: Any, value: String) {
        set(key.toString().toLowerCase(), value)
    }

    operator fun set(key: Any, value: Any) {
        set(key.toString(), value.toString())
    }

    operator fun set(key: String, value: Any) {
        set(key, value.toString())
    }

    override fun clone(): MutableHeaders {
        return MutableHeaders(HashMap(this.headers))
    }

    fun makeImmutable(): ImmutableHeaders {
        return ImmutableHeaders(this.headers)
    }
}