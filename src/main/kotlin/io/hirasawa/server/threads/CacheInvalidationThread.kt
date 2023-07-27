package io.hirasawa.server.threads

import io.hirasawa.server.Hirasawa
import java.time.Duration
import java.time.Instant

class CacheInvalidationThread: Runnable {
    override fun run() {
        val cacheMaps = arrayListOf(
            Hirasawa.beatmaps,
            Hirasawa.beatmapSets
        )

        for (map in cacheMaps) {
            map.purgeCacheOlderThan(Instant.now() - Duration.ofMinutes(10))
        }
    }
}