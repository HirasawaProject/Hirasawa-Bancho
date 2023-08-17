package io.hirasawa.server.plugin.event.remote

import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

class RemoteMessageReceivedEvent(val namespace: String,
                                 val key: String,
                                 val payload: String): HirasawaEvent<RemoteMessageReceivedEvent>, Cancelable()