package io.hirasawa.server.webserver.objects

import io.hirasawa.server.webserver.enums.HttpHeader

open class ImmutableHeaders(private val headers: HashMap<String, String>) {

    operator fun get(key: String): String? {
        return headers[key.lowercase()]
    }

    operator fun get(key: Any): String? {
        return get(key.toString())
    }

    operator fun contains(key: String): Boolean {
        return key.lowercase() in headers.keys
    }

    operator fun contains(key: Any): Boolean {
        return contains(key.toString())
    }

    operator fun iterator(): Iterator<MutableMap.MutableEntry<String, String>> {
        return headers.iterator()
    }

    open fun clone(): ImmutableHeaders {
        return ImmutableHeaders(this.headers)
    }

    override fun toString(): String {
        return headers.toString()
    }

    fun keys(): Set<String> {
        return headers.keys
    }
}