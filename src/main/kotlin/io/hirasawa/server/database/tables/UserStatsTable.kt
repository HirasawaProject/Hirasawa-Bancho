package io.hirasawa.server.database.tables

import org.jetbrains.exposed.sql.Table

object UserStatsTable: Table("user_stats") {
    val userId = integer("user_id")
    val rankedScore = long("ranked_score")
    val accuracy = float("accuracy")
    val playcount = integer("playcount")
    val totalScore = long("total_score")
    val rank = integer("rank")
    val pp = short("pp")
    val gamemode = integer("gamemode")
}