package io.hirasawa.server.database

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.objects.Score
import io.hirasawa.server.permissions.PermissionGroup
import java.lang.Exception
import java.util.HashMap

/**
 * Very non-optimised database engine running in memory
 * Please don't use this in production
 */
class MemoryDatabase(): Database(DatabaseCredentials()) {
    val users = ArrayList<User>()
    val friends = HashMap<Int, ArrayList<User>>()
    val groups = HashMap<String, PermissionGroup>()
    val scores = ArrayList<Score>()
    val beatmaps = ArrayList<Beatmap>()
    val beatmapSets = ArrayList<BeatmapSet>()



    override fun authenticate(username: String, password: String): Boolean {
        return true
    }

    override fun getUser(id: Int): User {
        for (user in users) {
            if (user.id == id) {
                return user
            }
        }

        throw Exception("Can't find user")
    }

    override fun getUser(username: String): User {
        for (user in users) {
            if (user.username == username) {
                return user
            }
        }

        throw Exception("Can't find user")
    }

    override fun getUserFriends(id: Int): ArrayList<User> {
        return if (id in friends.keys) {
            friends[id]!!
        } else {
            ArrayList<User>()
        }
    }

    override fun createPasswordHash(password: String): String {
        return password
    }

    override fun getPermissionGroups(): HashMap<String, PermissionGroup> {
        return groups
    }

    override fun getScore(id: Int): Score? {
        for (score in scores) {
            if (score.id == id) {
                return score
            }
        }

        return null
    }

    override fun getBeatmap(id: Int): Beatmap? {
        for (beatmap in beatmaps) {
            if (beatmap.id == id) {
                return beatmap
            }
        }

        return null
    }

    override fun getBeatmap(hash: String): Beatmap? {
        for (beatmap in beatmaps) {
            if (beatmap.hash == hash) {
                return beatmap
            }
        }

        return null
    }

    override fun getBeatmapSet(id: Int): BeatmapSet? {
        for (beatmapSet in beatmapSets) {
            if (beatmapSet.id == id) {
                return beatmapSet
            }
        }

        return null
    }

    override fun getBeatmapScores(beatmap: Beatmap, mode: GameMode, limit: Int): ArrayList<Score> {
        val listedScores = ArrayList<Score>()
        for (score in scores) {
            if (score.beatmapId == beatmap.id && score.gameMode == mode) {
                listedScores.add(score)
            }
        }

        scores.sortBy { it.score }

        return listedScores
    }

    override fun getUserScore(beatmap: Beatmap, mode: GameMode, user: User): Score? {
        for (score in scores) {
            if (score.user == user && score.beatmapId == beatmap.id && score.gameMode == mode) {
                return score
            }
        }

        return null
    }
}