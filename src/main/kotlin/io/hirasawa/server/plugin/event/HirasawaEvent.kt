package io.hirasawa.server.plugin.event

import io.hirasawa.server.Hirasawa

interface HirasawaEvent <out T: HirasawaEvent<T>> {
    @Suppress("UNCHECKED_CAST")
    fun call(): HirasawaEventCall<T> {
        Hirasawa.eventHandler.callEvent(this)
        return HirasawaEventCall(this as T)
    }
}