package io.hirasawa.server.lookupmaps

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.database.tables.BeatmapSetsTable
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.objects.Beatmap
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction

class BeatmapLookupMap: LookupMap<Beatmap>(Beatmap::class, BeatmapsTable.hash, BeatmapsTable.osuId) {

    override fun getKey(obj: Beatmap): String {
        return obj.hash
    }

    override fun getId(obj: Beatmap): Int {
        return obj.osuId
    }

    override fun lookupExternalObject(key: String?, id: Int?): Beatmap? {
        val queriedBeatmap = Hirasawa.osuApi.getBeatmaps(beatmapHash = key, beatmapId = id)
        if (queriedBeatmap.isEmpty()) return null
        val beatmaps = Hirasawa.osuApi.getBeatmaps(mapsetId = queriedBeatmap.first().beatmapsetId)
        if (beatmaps.isNotEmpty()) {
            if (!Hirasawa.rankBeatmapSet(beatmaps.first().beatmapsetId)) {
                return null
            }

            return if (key != null) {
                this[key]
            } else if (id != null) {
                this[id]
            } else {
                null
            }
        }
        return null
    }
}