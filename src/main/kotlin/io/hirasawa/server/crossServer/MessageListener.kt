package io.hirasawa.server.crossServer

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.plugin.event.remote.RemoteMessageReceivedEvent
import org.json.JSONObject
import redis.clients.jedis.JedisPubSub

class MessageListener: JedisPubSub() {
    override fun onPMessage(pattern: String?, channel: String?, message: String?) {
        if (channel == null || message == null) {
            return
        }
        if (":" !in channel) {
            return
        }

        val (namespace, key) = channel.split(":")

        RemoteMessageReceivedEvent(namespace, key, message).call().then {
            if (namespace == "hirasawa" && key == "event") {
                // Do Hirasawa event promotion
            }
        }
    }
}