package io.hirasawa.server.lookupmaps

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.database.tables.BeatmapSetsTable
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.osuapi.NoApiKeyException

class BeatmapSetLookupMap: LookupMap<BeatmapSet>(BeatmapSet::class, BeatmapSetsTable.title, BeatmapSetsTable.osuId) {
    override fun getKey(obj: BeatmapSet): String {
        return obj.title
    }

    override fun getId(obj: BeatmapSet): Int {
        return obj.osuId
    }

    override fun lookupExternalObject(key: String?, id: Int?): BeatmapSet? {
        val beatmaps = Hirasawa.osuApi.getBeatmaps(mapsetId = id)
        try {
            if (beatmaps.isNotEmpty()) {
                Hirasawa.rankBeatmapSet(beatmaps.first().beatmapsetId)

                return if (key != null) {
                    this[key]
                } else if (id != null) {
                    this[id]
                } else {
                    null
                }
            }
        } catch (noApiKey: NoApiKeyException) {
            return null
        }
        return null
    }
}