package io.hirasawa.server.lookupmaps

import java.time.Instant

/**
 * A caching friendly HashMap, this will store a timestamp alongside all objects making it possible to flush the cache
 * after a specific time
 */
class CacheMap<K, V> {
    private val hashMap = HashMap<K, V>()
    private val timestamps = HashMap<K, Instant>()

    val size
    get() = hashMap.size

    operator fun get(key: K): V? = hashMap[key]
    operator fun set(key: K, value: V) {
        hashMap[key] = value
        timestamps[key] = Instant.now()
    }
    operator fun contains(key: K): Boolean = key in hashMap

    fun remove(key: K) {
        hashMap.remove(key)
        timestamps.remove(key)
    }

    fun getInsertedTimestamp(key: K): Instant? {
        return timestamps[key]
    }

    fun getOlderThan(timestamp: Instant): Map<K, V> {
        return hashMap.filter {
            timestamps[it.key]!! < timestamp
        }
    }

    fun purgeOlderThan(timestamp: Instant): Int {
        var itemsRemoved = 0
        for (item in hashMap) {
            this.remove(item.key)
            itemsRemoved += 1
        }

        return itemsRemoved
    }

    fun getCacheInformation(): HashMap<K, Pair<Instant, V>> {
        val cacheInformation = HashMap<K, Pair<Instant, V>>()

        for (item in hashMap) {
            cacheInformation[item.key] = Pair(timestamps[item.key]!!, item.value)
        }

        return cacheInformation
    }
}