package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.database.tables.BeatmapSetsTable
import io.hirasawa.server.enums.DefaultRankingState
import io.hirasawa.server.osuapi.NoApiKeyException
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

        var defaultRankingState = Hirasawa.config.defaultRankingState
        if (defaultRankingState in listOf(DefaultRankingState.UNKNOWN, DefaultRankingState.NOT_SUBMITTED)) {
           defaultRankingState = DefaultRankingState.RANK_ALL
        }

        try {
            if (Hirasawa.rankBeatmapSet(mapsetId, defaultRankingState)) {
                context.respond("Beatmap has now been ranked")
            } else{
                context.respond("Failed to rank beatmap")
            }
        } catch (noApi: NoApiKeyException) {
            context.respond("No API key is defined for osu! API")
        }


        return true
    }

}