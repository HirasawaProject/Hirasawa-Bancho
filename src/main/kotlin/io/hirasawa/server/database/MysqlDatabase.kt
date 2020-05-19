package io.hirasawa.server.database

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import org.mindrot.jbcrypt.BCrypt
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

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

    override fun getUser(id: Int): User {
        val query = "SELECT * FROM users WHERE username = ?"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)

        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return BanchoUser(resultSet.getInt("id"), resultSet.getString("username"), 0, 0,
                0, GameMode.OSU,0F,0F,0, UUID.randomUUID())
        }

        throw Exception("User not found")
    }

    override fun getUser(username: String): User {
        val query = "SELECT * FROM users WHERE username = ?"
        val statement = connection.prepareStatement(query)
        statement.setString(1, username)

        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return BanchoUser(resultSet.getInt("id"), resultSet.getString("username"), 0, 0,
                127, GameMode.OSU,0F,0F,0, UUID.randomUUID())
        }

        throw Exception("User not found")
    }

    override fun createPasswordHash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}