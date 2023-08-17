package io.hirasawa.server.crossServer

import io.hirasawa.server.Hirasawa
import redis.clients.jedis.Jedis

class CrossServerManager {
    private val jedis = Jedis(Hirasawa.config.crossServerCredentials.host, Hirasawa.config.crossServerCredentials.port)

    init {
        jedis.connect()
    }

    fun listen() {
        jedis.psubscribe(MessageListener(), "*")
    }
}