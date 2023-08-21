package io.hirasawa.server.bancho.user

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.chat.command.CommandSender
import io.hirasawa.server.chat.message.PrivateChatMessage
import io.hirasawa.server.database.tables.FriendsTable
import io.hirasawa.server.database.tables.PermissionGroupUserTable
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
            (PermissionGroupUserTable innerJoin PermissionGroupsTable).select {
                PermissionGroupUserTable.userId eq userId
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

    /**
     * Sends a private chat message to this user
     *
     * @param from The user that sent the chat message
     * @param message The message sent to the user
     */
    fun sendPrivateMessage(from: User, message: String) {
        Hirasawa.chatEngine.handleChat(PrivateChatMessage(from, this, message))
    }

    /**
     * Revokes the ChatChannel from the user, this will cause the channel to be removed from the user's client
     */
    abstract fun revokeChatChannel(chatChannel: ChatChannel)

    /**
     * Adds a ChatChannel to the user, this will cause the channel to be added to the user's client
     *
     * If the client supports autojoin, this will automatically join the user to the channel
     */
    abstract fun addChannel(chatChannel: ChatChannel)
}