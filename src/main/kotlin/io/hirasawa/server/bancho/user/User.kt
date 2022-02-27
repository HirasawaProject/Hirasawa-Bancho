package io.hirasawa.server.bancho.user

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.command.CommandSender
import io.hirasawa.server.database.tables.FriendsTable
import io.hirasawa.server.database.tables.PermissionGroupUsersTable
import io.hirasawa.server.database.tables.PermissionGroupsTable
import io.hirasawa.server.database.tables.UsersTable
import io.hirasawa.server.permissions.PermissionGroup
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * A base user, this is used to extend into classes with more features
 */
abstract class User(val id: Int, val username: String, val timezone: Byte, val countryCode: Byte ,val longitude: Float,
                    val latitude: Float, val isBanned: Boolean): CommandSender {
    abstract fun sendPrivateMessage(from: User, message: String)
    val friends: ArrayList<User> by lazy {
        val arrayList = ArrayList<User>()
        val userId = this.id
        transaction {
            (FriendsTable innerJoin UsersTable).select {
                (FriendsTable.userId eq userId)
            }.forEach {
                arrayList.add(BanchoUser(it))
            }
        }

        arrayList
    }
    val permissionGroups: ArrayList<PermissionGroup> by lazy {
        val permissionGroups = ArrayList<PermissionGroup>()
        val userId = this.id
        transaction {
            (PermissionGroupUsersTable innerJoin PermissionGroupsTable).select {
                PermissionGroupUsersTable.userId eq userId
            }.forEach {
                permissionGroups.add(Hirasawa.permissionEngine.getGroup(it[PermissionGroupsTable.name]))
            }
        }

        permissionGroups
    }

    fun hasPermission(node: String): Boolean {
        return Hirasawa.permissionEngine.hasPermission(this, node)
    }

    fun addGroup(group: PermissionGroup) {
        permissionGroups.add(group)
    }

    fun removeGroup(group: PermissionGroup) {
        permissionGroups.remove(group)
    }
}