package io.hirasawa.server.objects

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import java.util.*
import kotlin.collections.HashMap

/**
 * Basic map that contains a list of users that creates an id, username and UUID cache to easily reference them
 */
class UserMap<T: User> {
    private val uuidCache = HashMap<UUID, BanchoUser>()
    private val usernameCache = HashMap<String, T>()
    private val idCache = HashMap<Int, T>()

    val usernameKeys get() = usernameCache.keys
    val uuidKeys get() = uuidCache.keys
    val idKeys get() = idCache.keys
    val values get() = idCache.values

    operator fun get(key: UUID): BanchoUser? {
        return uuidCache[key]
    }

    operator fun get(key: String): T? {
        return usernameCache[key]
    }

    operator fun get(key: Int): T? {
        return idCache[key]
    }

    operator fun get(user: T): T? {
        return idCache[user.id]
    }

    operator fun iterator(): MutableIterator<T> {
        return idCache.values.iterator()
    }

    fun add(user: T) {
        if (user is BanchoUser) {
            uuidCache[user.uuid] = user
        }
        usernameCache[user.username] = user
        idCache[user.id] = user
        Hirasawa.chatEngine.addUser(user)
    }

    fun remove(user: T) {
        if (user is BanchoUser)  {
            uuidCache.remove(user.uuid)
        }
        usernameCache.remove(user.username)
        idCache.remove(user.id)
        Hirasawa.chatEngine.removeUser(user)
    }

    operator fun contains(key: UUID): Boolean {
        return key in uuidCache.keys
    }

    operator fun contains(key: String): Boolean {
        return key in usernameCache.keys
    }

    operator fun contains(key: Int): Boolean {
        return key in idCache.keys
    }

    operator fun contains(user: T): Boolean {
        if (user is BanchoUser) {
            return user.uuid in uuidCache.keys
        }
        return user.id in idCache.keys
    }
}