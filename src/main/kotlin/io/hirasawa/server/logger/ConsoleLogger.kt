package io.hirasawa.server.logger

class ConsoleLogger: Logger() {
    override fun log(message: Any) {
        println("$timestamp $message")
    }
}