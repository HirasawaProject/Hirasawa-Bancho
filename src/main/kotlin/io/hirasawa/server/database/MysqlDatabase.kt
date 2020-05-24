package io.hirasawa.server.database

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.objects.Score
import io.hirasawa.server.permissions.PermissionGroup
import org.mindrot.jbcrypt.BCrypt
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*
import kotlin.collections.ArrayList

class MysqlDatabase(credentials: DatabaseCredentials) : Database(credentials) {
    private var connection: Connection
    init {
        Class.forName("com.mysql.jdbc.Driver")
        connection = DriverManager.getConnection(
            "jdbc:mysql://${credentials.host}/${credentials.database}" +
                    "?user=${credentials.username}&password=${credentials.password}")

    }

    override fun authenticate(username: String, password: String): Boolean {
        val query = "SELECT username, password FROM users WHERE username = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, username)

        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return BCrypt.checkpw(password, resultSet.getString("password"))
        }

        return false
    }

    private fun getPermissionGroupsFromUser(userId: Int): ArrayList<PermissionGroup> {
        val query = "SELECT name FROM permission_group_users INNER JOIN permission_groups ON " +
                "permission_groups.id = group_id  WHERE user_id = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, userId)

        val groups = ArrayList<PermissionGroup>()

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            groups.add(Hirasawa.permissionEngine.getGroup(resultSet.getString("name")))
        }

        return groups

    }

    private fun resultSetToUser(resultSet: ResultSet): User {
        return BanchoUser(resultSet.getInt("users.id"), resultSet.getString("users.username"), 0, 0,
            getPermissionGroupsFromUser(resultSet.getInt("users.id")), GameMode.OSU,0F,0F,
            UUID.randomUUID(), resultSet.getBoolean("users.banned"))
    }

    override fun getUser(id: Int): User {
        val query = "SELECT * FROM users WHERE id = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return resultSetToUser(resultSet)
        }

        throw Exception("User not found")
    }

    override fun getUser(username: String): User {
        val query = "SELECT * FROM users WHERE username = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, username)

        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return resultSetToUser(resultSet)
        }

        throw Exception("User not found")
    }

    override fun createPasswordHash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    override fun getUserFriends(id: Int): ArrayList<User> {
        val query = "SELECT * FROM friends INNER JOIN users ON friend_id = users.id WHERE user_id = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)

        val friends = ArrayList<User>()

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            friends.add(resultSetToUser(resultSet))
        }

        return friends
    }

    override fun getPermissionGroups(): HashMap<String, PermissionGroup> {
        val query = "SELECT name, node from permission_groups LEFT JOIN permission_nodes ON group_id = " +
                "permission_groups.id;"
        val statement = connection.prepareStatement(query)

        val groups = HashMap<String, PermissionGroup>()

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            val name = resultSet.getString("name")
            val node = resultSet.getString("node")

            if (name !in groups) {
                groups[name] = PermissionGroup(name)
            }
            groups[name]?.permissions?.add(node)
        }

        return groups
    }

    private fun resultSetToScore(resultSet: ResultSet): Score {
        return resultSetToScore(resultSet, resultSetToUser(resultSet))
    }

    private fun resultSetToScore(resultSet: ResultSet, user: User): Score {
        return Score(resultSet.getInt("scores.id"), user, resultSet.getInt("scores.score"),
            resultSet.getInt("scores.combo"), resultSet.getInt("scores.count50"), resultSet.getInt("scores.count100"),
            resultSet.getInt("scores.count300"), resultSet.getInt("scores.count_miss"),
            resultSet.getInt("scores.count_katu"), resultSet.getInt("scores.count_geki"),
            resultSet.getBoolean("scores.full_combo"), resultSet.getInt("scores.mods"),
            resultSet.getInt("scores.timestamp"), GameMode.values()[resultSet.getInt("scores.gamemode")],
            resultSet.getInt("scores.rank"))
    }

    override fun getScore(id: Int): Score? {
        val query = "SELECT * FROM scores LEFT JOIN users ON user_id = users.id WHERE scores.id = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return resultSetToScore(resultSet)
        }

        return null
    }

    private fun resultSetToBeatmap(resultSet: ResultSet): Beatmap {
        return Beatmap(resultSet.getInt("beatmaps.id"), resultSet.getInt("beatmaps.mapset_id"),
            resultSet.getString("beatmaps.difficulty"), resultSet.getString("beatmaps.hash"),
            resultSet.getInt("beatmaps.ranks"), resultSet.getFloat("beatmaps.offset"))
    }

    override fun getBeatmap(id: Int): Beatmap? {
        val query = "SELECT * FROM beatmaps WHERE id = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return resultSetToBeatmap(resultSet)
        }

        return null
    }

    override fun getBeatmap(hash: String): Beatmap? {
        val query = "SELECT * FROM beatmaps WHERE hash = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, hash)

        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return resultSetToBeatmap(resultSet)
        }

        return null
    }

    override fun getBeatmapSet(id: Int): BeatmapSet? {
        val query = "SELECT * FROM beatmapsets WHERE id = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return BeatmapSet(resultSet.getInt("id"), resultSet.getString("artist"), resultSet.getString("title"),
                BeatmapStatus.fromId(resultSet.getInt("status")))
        }

        return null
    }

    override fun getBeatmapScores(beatmap: Beatmap, mode: GameMode, limit: Int): ArrayList<Score> {
        val query = "SELECT * FROM scores LEFT JOIN users ON user_id = users.id WHERE beatmap_id = ? AND " +
                "gamemode = ? ORDER BY score DESC LIMIT ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, beatmap.id)
        statement.setInt(2, mode.ordinal)
        statement.setInt(3, limit)

        val scores = ArrayList<Score>()

        val resultSet = statement.executeQuery()
        while (resultSet.next()) {
            val score = resultSetToScore(resultSet)
            scores.add(score)
        }

        return scores
    }

    override fun getUserScore(beatmap: Beatmap, mode: GameMode, user: User): Score? {
        val query = "SELECT * FROM scores WHERE user_id = ? AND beatmap_id = ? AND gamemode = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, user.id)
        statement.setInt(2, beatmap.id)
        statement.setInt(3, mode.ordinal)

        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return resultSetToScore(resultSet, user)
        }

        return null
    }

}