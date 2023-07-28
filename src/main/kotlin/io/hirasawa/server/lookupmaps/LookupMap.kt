package io.hirasawa.server.lookupmaps

import io.hirasawa.server.Hirasawa
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import kotlin.reflect.KClass

abstract class LookupMap<T>(private val modelClass: KClass<*>,
                            private val keyField: Column<String>,
                            private val idField: Column<Int>): ILookupMap<T> {
    private val idCache = CacheMap<Int, T>()
    private val keyCache = CacheMap<String, T>()

    override operator fun get(key: String): T? = get(key = key, id = null)
    override operator fun get(id: Int): T? = get(key = null, id = id)

    private fun get(key: String? = null, id: Int? = null): T? {
        if (key != null && key in keyCache) {
            return keyCache[key]
        }
        if (id != null && id in idCache) {
            return idCache[id]
        }

        val obj = lookupObject(key = key, id = id)
        if (obj != null) {
            idCache[getId(obj)] = obj
            keyCache[getKey(obj)] = obj
        }
        return obj
    }

    private fun lookupObject(key: String? = null, id: Int? = null): T? {
        if (key == null && id == null) {
            return null
        }

        val result = transaction {
            // Create empty query
            var query = keyField.table.selectAll()

            key?.let {
                query = query.andWhere {
                    keyField eq key
                }
            }
            id?.let {
                query = query.andWhere {
                    idField eq id
                }
            }

            query.firstOrNull()
        } ?: return lookupExternalObject(key, id)

        return Hirasawa.databaseToObject(modelClass, result)
    }

    fun purgeCacheOlderThan(timestamp: Instant): Int {
        val amount = idCache.purgeOlderThan(timestamp)
        keyCache.purgeOlderThan(timestamp)

        return amount
    }

    fun getIdCacheInformation() = idCache.getCacheInformation()
    fun getKeyCacheInformation() = keyCache.getCacheInformation()
    protected abstract fun lookupExternalObject(key: String? = null, id: Int? = null): T?
    protected abstract fun getId(obj: T): Int
    protected abstract fun getKey(obj: T): String
}