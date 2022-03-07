package io.hirasawa.server.irc

import io.hirasawa.server.bancho.chat.message.ChatMessage
import io.hirasawa.server.bancho.chat.message.GlobalChatMessage
import io.hirasawa.server.bancho.chat.message.PrivateChatMessage
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.irc.clientcommands.*
import io.hirasawa.server.irc.objects.IrcUser
import io.hirasawa.server.irc.servercommands.IrcServerCommand
import io.hirasawa.server.irc.threads.IrcServerThread
import java.io.DataOutputStream
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class IrcServer(private val port: Int) {
    private val outputStreams = HashMap<User, DataOutputStream>()
    private val connectedUsers = ArrayList<IrcUser>()
    private val registeredCommands = HashMap<String, IrcServerCommand>()

    fun start() {
        Thread(IrcServerThread(port)).start()
    }

    fun sendToAll(ircProtocolReply: IrcProtocolReply) {
        for (streams in outputStreams) {
            streams.value.writeBytes(ircProtocolReply.generate())
        }
    }

    fun sendToUser(user: User, ircProtocolReply: IrcProtocolReply) {
        outputStreams[user]?.writeBytes(ircProtocolReply.generate())
    }

    fun handleCommand(ircUser: IrcUser, command: String, args: Array<String>) {
        println("Handle: $command")
        registeredCommands[command.uppercase()]?.handle(ircUser, command, args)
    }

    fun addUser(ircUser: IrcUser, outputStream: DataOutputStream) {
        connectedUsers.add(ircUser)
        outputStreams[ircUser] = outputStream

        sendToUser(ircUser, RplWelcome(ircUser, "Welcome to Hirasawa"))
        sendToUser(ircUser, RplYourHost(ircUser))
        sendToUser(ircUser, RplCreated(ircUser))
        sendToUser(ircUser, RplMyInfo(ircUser))
        sendToUser(ircUser, RplLUserClient(ircUser))
        sendToUser(ircUser, RplMotdStart(ircUser, "Welcome"))
        sendToUser(ircUser, RplMotd(ircUser, "to"))
        sendToUser(ircUser, RplEndOfMotd(ircUser, "Hirasawa~"))
    }

    fun removeUser(ircUser: IrcUser) {
        connectedUsers.remove(ircUser)
        outputStreams[ircUser]?.close()
        outputStreams.remove(ircUser)
    }

    fun registerServerCommand(command: String, ircServerCommand: IrcServerCommand) {
        registeredCommands[command.uppercase()] = ircServerCommand
    }
}