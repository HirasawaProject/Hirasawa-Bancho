package io.hirasawa.server.database

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.config.HirasawaConfig
import io.hirasawa.server.permissions.PermissionGroup
import org.mindrot.jbcrypt.BCrypt
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.*
import kotlin.collections.ArrayList

class MysqlDatabase(credentials: DatabaseCredentials) : Database(credentials) {
    private lateinit var connection: Connection
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
        return BanchoUser(resultSet.getInt("id"), resultSet.getString("username"), 0, 0,
            getPermissionGroupsFromUser(resultSet.getInt("id")), GameMode.OSU,0F,0F, UUID.randomUUID(),
            resultSet.getBoolean("banned"))
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

}