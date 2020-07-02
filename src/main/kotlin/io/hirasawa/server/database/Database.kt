package io.hirasawa.server.database

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.objects.Score
import io.hirasawa.server.permissions.PermissionGroup
import java.math.BigInteger
import java.security.MessageDigest
import java.util.HashMap


abstract class Database(protected val credentials: DatabaseCredentials) {
    abstract fun authenticate(username: String, password: String): Boolean
    abstract fun getUser(id: Int): User?
    abstract fun getUser(username: String): User?
    abstract fun getUserFriends(id: Int): ArrayList<User>

    fun authenticateWithMd5(username: String, password: String): Boolean {
        val messageDigest = MessageDigest.getInstance("MD5")
        val bytes = messageDigest.digest(password.toByteArray())
        val number = BigInteger(1, bytes)
        val hashText = number.toString(16).toLowerCase()

        return authenticate(username, hashText)
    }

    abstract fun createPasswordHash(password: String): String
    abstract fun getPermissionGroups(): HashMap<String, PermissionGroup>
    abstract fun getScore(id: Int): Score?
    abstract fun getBeatmap(id: Int): Beatmap?
    abstract fun getBeatmap(hash: String): Beatmap?
    abstract fun getBeatmapSet(id: Int): BeatmapSet?
    abstract fun getBeatmapScores(beatmap: Beatmap, mode: GameMode, limit: Int): ArrayList<Score>
    abstract fun getUserScore(beatmap: Beatmap, mode: GameMode, user: User): Score?
    abstract fun getUserScores(mode: GameMode, user: User): ArrayList<Score>
    abstract fun getUserStats(user: User, gameMode: GameMode): UserStats?
    abstract fun getUserStats(gameMode: GameMode, sort: String = "pp"): ArrayList<UserStats>
    abstract fun submitScore(score: Score)
    abstract fun removeScore(score: Score)
    abstract fun processLeaderboard(beatmap: Beatmap, gameMode: GameMode)
    abstract fun updateScore(newScore: Score)
    abstract fun updateBeatmap(newBeatmap: Beatmap)
    abstract fun updateUserStats(userStats: UserStats)
    abstract fun processGlobalLeaderboard(gameMode: GameMode)
    abstract fun getBeatmapSets(page: Int, limit: Int, sort: String, search: String): ArrayList<BeatmapSet>
    abstract fun getBeatmapSetAmount(): Int
    abstract fun getBeatmatSetDifficulties(beatmapSet: BeatmapSet): ArrayList<Beatmap>
}