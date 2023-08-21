package io.hirasawa.server.objects

import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Basic map that contains a list of users that creates an id, username and UUID cache to easily reference them
 */
class UserMap<T: User> {
    private val uuidCache = HashMap<UUID, BanchoUser>()
    private val usernameCache = HashMap<String, T>()
    private val idCache = HashMap<Int, T>()
    private val contents = ArrayList<T>()
    private val binds = HashMap<BindType, HashMap<Int, (user: T?) -> Unit>>()

    val usernameKeys get() = usernameCache.keys
    val uuidKeys get() = uuidCache.keys
    val idKeys get() = idCache.keys
    val values get() = contents
    val size get() = contents.size

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
        return values.iterator()
    }

    fun add(user: T) {
        if (user is BanchoUser) {
            uuidCache[user.uuid] = user
        }
        usernameCache[user.username] = user
        idCache[user.id] = user
        values.add(user)

        binds[BindType.ADD]?.forEach {
            it.value(user)
        }
    }

    fun remove(user: T) {
        if (user is BanchoUser)  {
            uuidCache.remove(user.uuid)
        }
        usernameCache.remove(user.username)
        idCache.remove(user.id)
        values.remove(user)

        binds[BindType.REMOVE]?.forEach {
            it.value(user)
        }
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
        return user in values
    }

    fun bind(bindType: BindType, function: (user: T?) -> Unit): Int {
        if (bindType !in binds.keys) {
            binds[bindType] = HashMap()
        }

        val id = binds[bindType]?.size ?: return -1
        binds[bindType]?.set(id, function)
        return id
    }

    fun unbind(bindType: BindType, id: Int) {
        binds[bindType]?.remove(id)
    }

    fun close() {
        binds[BindType.CLOSE]?.forEach {
            it.value(null)
        }
    }

    enum class BindType {
        ADD, REMOVE, CLOSE
    }
}