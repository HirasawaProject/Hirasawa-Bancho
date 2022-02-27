package io.hirasawa.server.plugin.event

import io.hirasawa.server.Hirasawa

interface HirasawaEvent <out T: HirasawaEvent<T>> {
    fun call(): T {
        Hirasawa.eventHandler.callEvent(this)
        return this as T
    }

    fun then(body: (event: T) -> Unit): T {
        if (this is Cancelable) {
            if (!isCancelled) {
                body.invoke(this as T)
            }
        } else {
            body.invoke(this as T)
        }
        return this as T
    }

    fun cancelled(body: (event: T) -> Unit): T {
        if (this is Cancelable) {
            if (isCancelled) {
                body.invoke(this as T)
            }
        }
        return this as T
    }
}