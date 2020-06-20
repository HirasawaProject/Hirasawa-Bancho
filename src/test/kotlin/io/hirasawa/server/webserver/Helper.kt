package io.hirasawa.server.webserver

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.objects.Score
import io.hirasawa.server.permissions.PermissionGroup
import java.util.*
import kotlin.collections.ArrayList

class Helper {
    companion object {
        fun createUser(id: Int, username: String): User {
            return BanchoUser(id, username, 0, 0, ArrayList<PermissionGroup>(), 0F,
                0F, UUID.randomUUID(), false)
        }

        fun createScore(id: Int, user: User, score: Int, rank: Int, beatmapId: Int, gamemode: GameMode): Score {
            return Score(id, user, score, 100, 50, 100, 300, 0, 10,
                10, true, 0, 0, gamemode, rank, beatmapId, 1F)
        }

        fun createBeatmap(id: Int, mapsetId: Int, hash: String, ranks: Int): Beatmap {
            return Beatmap(id, mapsetId, "HARD", hash, ranks, 0F)
        }

        fun createBeatmapSet(id: Int, artist: String, title: String, status: BeatmapStatus): BeatmapSet {
            return BeatmapSet(id, artist, title, status)
        }

        fun createUserStats(userId: Int, gamemode: GameMode): UserStats {
            return UserStats(userId, 0, 0F, 0, 0, 0, 0, gamemode)
        }

        fun userToBanchoUser(user: User): BanchoUser {
            return BanchoUser(user.id, user.username, user.timezone, user.countryCode, user.permissionGroups,
                user.longitude, user.latitude, UUID.randomUUID(), user.isBanned)
        }
    }
}