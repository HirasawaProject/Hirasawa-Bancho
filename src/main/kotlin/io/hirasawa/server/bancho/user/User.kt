package io.hirasawa.server.bancho.user

/**
 * A base user, this is used to extend into classes with more features
 */
abstract class User(val id: Int, val username: String) {
    abstract fun onMessage(from: User, channel: String, message: String)
}