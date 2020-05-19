package io.hirasawa.server.bancho.objects

import io.hirasawa.server.bancho.user.BanchoUser
import java.util.*
import kotlin.collections.HashMap

class BanchoUserMap {
    private val uuidCache = HashMap<UUID, BanchoUser>()
    private val usernameCache = HashMap<String, BanchoUser>()
    private val idCache = HashMap<Int, BanchoUser>()

    operator fun get(key: UUID): BanchoUser? {
        return uuidCache[key]
    }

    operator fun get(key: String): BanchoUser? {
        return usernameCache[key]
    }

    operator fun get(key: Int): BanchoUser? {
        return idCache[key]
    }

    fun add(user: BanchoUser) {
        uuidCache[user.uuid] = user
        usernameCache[user.username] = user
        idCache[user.id] = user
    }

    fun remove(user: BanchoUser) {
        uuidCache.remove(user.uuid)
        usernameCache.remove(user.username)
        idCache.remove(user.id)
    }
}