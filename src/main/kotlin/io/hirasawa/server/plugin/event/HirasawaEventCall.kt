package io.hirasawa.server.plugin.event

class HirasawaEventCall<out T: HirasawaEvent<*>>(val event: T) {
    val isCancelled: Boolean
    get() {
        return if (event is Cancelable) {
            event.isCancelled
        } else {
            false
        }
    }

    fun then(body: (event: T) -> Unit): HirasawaEventCall<T> {
        if (event is Cancelable) {
            if (!event.isCancelled) {
                body.invoke(event)
            }
        } else {
            body.invoke(event)
        }
        return this
    }

    fun cancelled(body: (event: T) -> Unit): HirasawaEventCall<T> {
        if (event is Cancelable) {
            if (event.isCancelled) {
                body.invoke(event)
            }
        }
        return this
    }
}