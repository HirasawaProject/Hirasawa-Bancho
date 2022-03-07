package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.BeatmapsetsTable
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
            BeatmapsetsTable.select {
                BeatmapsetsTable.osuId eq mapsetId
            }.count()
        } > 0) {
            context.respond("Mapset already exists")
            return false
        }

        val beatmaps = Hirasawa.osuApi.getBeatmaps(mapsetId = mapsetId)

        transaction {
            val id = BeatmapsetsTable.insertAndGetId {
                it[BeatmapsetsTable.artist] = beatmaps.first().artist
                it[BeatmapsetsTable.title] = beatmaps.first().title
                it[BeatmapsetsTable.status] = 3
                it[BeatmapsetsTable.osuId] = beatmaps.first().beatmapsetId
                it[BeatmapsetsTable.mapperName] = beatmaps.first().creator
                it[BeatmapsetsTable.genreId] = beatmaps.first().genreId
                it[BeatmapsetsTable.languageId] = beatmaps.first().languageId
                it[BeatmapsetsTable.rating] = beatmaps.first().rating
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