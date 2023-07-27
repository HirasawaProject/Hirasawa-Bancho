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
            transaction {
                val setId = BeatmapSetsTable.insertAndGetId {
                    it[artist] = beatmaps.first().artist
                    it[title] = beatmaps.first().title
                    it[status] = BeatmapStatus.RANKED.ordinal
                    it[osuId] = beatmaps.first().beatmapsetId
                    it[mapperName] = beatmaps.first().creator
                    it[genreId] = beatmaps.first().genreId
                    it[languageId] = beatmaps.first().languageId
                    it[rating] = beatmaps.first().rating
                }.value

                commit()

                for (beatmap in beatmaps) {
                    BeatmapsTable.insert {
                        it[mapsetId] = setId
                        it[difficulty] = beatmap.version
                        it[hash] = beatmap.fileMd5
                        it[ranks] = 0
                        it[offset] = 0F
                        it[osuId] = beatmap.beatmapId
                        it[totalLength] = beatmap.totalLength
                        it[hitLength] = beatmap.hitLength
                        it[circleSize] = beatmap.diffSize
                        it[overallDifficulty] = beatmap.diffOverall
                        it[approachRate] = beatmap.diffApproach
                        it[healthDrain] = beatmap.diffDrain
                        it[gamemode] = beatmap.mode
                        it[countNormal] = beatmap.countNormal
                        it[countSlider] = beatmap.countSlider
                        it[countSpinner] = beatmap.countSpinner
                        it[bpm] = beatmap.bpm
                        it[hasStoryboard] = beatmap.storyboard
                        it[maxCombo] = beatmap.maxCombo
                        it[playCount] = 0
                        it[passCount] = 0
                    }
                }
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