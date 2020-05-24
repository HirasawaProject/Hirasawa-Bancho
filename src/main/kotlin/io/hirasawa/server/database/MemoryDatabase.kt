package io.hirasawa.server.database

import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.permissions.PermissionGroup
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap

/**
 * Very non-optimised database engine running in memory
 * Please don't use this in production
 */
class MemoryDatabase(): Database(DatabaseCredentials()) {
    val users = ArrayList<User>()
    val friends = HashMap<Int, ArrayList<User>>()
    val groups = HashMap<String, PermissionGroup>()


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
}