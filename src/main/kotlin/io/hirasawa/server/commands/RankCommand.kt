package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.BeatmapSetsTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class RankCommand: ChatCommand("rank", "Automatically rank beatmaps from osu!", "hirasawa.command.rank") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        if (args.isEmpty()) {
            context.respond("!rank <mapset id>")
            return false
        }

        val mapsetId = args[0].toInt()

        if (transaction {
            BeatmapSetsTable.select {
                BeatmapSetsTable.osuId eq mapsetId
            }.count()
        } > 0) {
            context.respond("Mapset already exists")
            return false
        }

        val beatmaps = Hirasawa.osuApi.getBeatmaps(mapsetId = mapsetId)

        transaction {
            val id = BeatmapSetsTable.insertAndGetId {
                it[BeatmapSetsTable.artist] = beatmaps.first().artist
                it[BeatmapSetsTable.title] = beatmaps.first().title
                it[BeatmapSetsTable.status] = 3
                it[BeatmapSetsTable.osuId] = beatmaps.first().beatmapsetId
                it[BeatmapSetsTable.mapperName] = beatmaps.first().creator
                it[BeatmapSetsTable.genreId] = beatmaps.first().genreId
                it[BeatmapSetsTable.languageId] = beatmaps.first().languageId
                it[BeatmapSetsTable.rating] = beatmaps.first().rating
            }

            commit()

            for (beatmap in beatmaps) {
                BeatmapsTable.insert {
                    it[BeatmapsTable.mapsetId] = id.value
                    it[BeatmapsTable.difficulty] = beatmap.version
                    it[BeatmapsTable.hash] = beatmap.fileMd5
                    it[BeatmapsTable.ranks] = 0
                    it[BeatmapsTable.offset] = 0F
                    it[BeatmapsTable.osuId] = beatmap.beatmapId
                    it[BeatmapsTable.totalLength] = beatmap.totalLength
                    it[BeatmapsTable.hitLength] = beatmap.hitLength
                    it[BeatmapsTable.circleSize] = beatmap.diffSize
                    it[BeatmapsTable.overallDifficulty] = beatmap.diffOverall
                    it[BeatmapsTable.approachRate] = beatmap.diffApproach
                    it[BeatmapsTable.healthDrain] = beatmap.diffDrain
                    it[BeatmapsTable.gamemode] = beatmap.mode
                    it[BeatmapsTable.countNormal] = beatmap.countNormal
                    it[BeatmapsTable.countSlider] = beatmap.countSlider
                    it[BeatmapsTable.countSpinner] = beatmap.countSpinner
                    it[BeatmapsTable.bpm] = beatmap.bpm
                    it[BeatmapsTable.hasStoryboard] = beatmap.storyboard
                    it[BeatmapsTable.maxCombo] = beatmap.maxCombo
                    it[BeatmapsTable.playCount] = 0
                    it[BeatmapsTable.passCount] = 0
                }
            }
        }

        context.respond("Beatmap has now been ranked")

        return true
    }

}