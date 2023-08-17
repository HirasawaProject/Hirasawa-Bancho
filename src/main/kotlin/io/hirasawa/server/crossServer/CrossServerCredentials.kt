package io.hirasawa.server.crossServer

data class CrossServerCredentials (val host: String, val port: Int) {
    constructor(): this("localhost", 6379)
}