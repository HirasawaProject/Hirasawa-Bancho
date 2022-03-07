package io.hirasawa.server.irc

import io.hirasawa.server.Hirasawa
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
    val connectedUsers = ArrayList<IrcUser>()
    private val registeredCommands = HashMap<String, IrcServerCommand>()

    fun start() {
        Thread(IrcServerThread(port)).start()
    }

    fun sendToAll(ircProtocolReply: IrcProtocolReply) {
        for (streams in outputStreams) {
            streams.value.writeBytes(ircProtocolReply.generate(streams.key))
        }
    }

    fun sendToUser(user: User, ircProtocolReply: IrcProtocolReply) {
        outputStreams[user]?.writeBytes(ircProtocolReply.generate(user))
    }

    fun handleCommand(ircUser: IrcUser, command: String, args: Array<String>) {
        println("Handle: $command")
        registeredCommands[command.uppercase()]?.handle(ircUser, command, args)
    }

    fun addUser(ircUser: IrcUser, outputStream: DataOutputStream) {
        connectedUsers.add(ircUser)
        outputStreams[ircUser] = outputStream
        Hirasawa.chatEngine.addUser(ircUser)

        sendToUser(ircUser, RplWelcome("Welcome to Hirasawa"))
        sendToUser(ircUser, RplYourHost())
        sendToUser(ircUser, RplCreated())
        sendToUser(ircUser, RplMyInfo())
        sendToUser(ircUser, RplLUserClient())
        sendToUser(ircUser, RplMotdStart("Welcome"))
        sendToUser(ircUser, RplMotd("to"))
        sendToUser(ircUser, RplEndOfMotd("Hirasawa~"))
    }

    fun removeUser(ircUser: IrcUser) {
        connectedUsers.remove(ircUser)
        outputStreams[ircUser]?.close()
        outputStreams.remove(ircUser)
        Hirasawa.chatEngine.removeUser(ircUser)
    }

    fun registerServerCommand(command: String, ircServerCommand: IrcServerCommand) {
        registeredCommands[command.uppercase()] = ircServerCommand
    }
}