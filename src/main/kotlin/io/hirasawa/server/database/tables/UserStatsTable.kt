package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable

object UserStatsTable: LaravelTable("user_stats") {
    val userId = integer("user_id").references(UsersTable.id)
    val rankedScore = long("ranked_score")
    val accuracy = float("accuracy")
    val playCount = integer("play_count")
    val totalScore = long("total_score")
    val rank = integer("rank")
    val pp = short("pp")
    val mode = integer("mode")
}