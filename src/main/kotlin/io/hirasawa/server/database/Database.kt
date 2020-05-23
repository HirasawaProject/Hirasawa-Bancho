package io.hirasawa.server.database

import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.permissions.PermissionGroup
import java.math.BigInteger
import java.security.MessageDigest
import java.util.ArrayList
import java.util.HashMap


abstract class Database(protected val credentials: DatabaseCredentials) {
    abstract fun authenticate(username: String, password: String): Boolean
    abstract fun getUser(id: Int): User
    abstract fun getUser(username: String): User
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
}