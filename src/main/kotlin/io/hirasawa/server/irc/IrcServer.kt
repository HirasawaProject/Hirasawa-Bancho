package io.hirasawa.server.irc

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.irc.clientcommands.*
import io.hirasawa.server.irc.objects.IrcUser
import io.hirasawa.server.irc.servercommands.IrcServerCommand
import io.hirasawa.server.irc.threads.IrcServerThread
import io.hirasawa.server.irc.threads.IrcUserTimeoutThread
import io.hirasawa.server.plugin.event.irc.IrcUserLoginEvent
import io.hirasawa.server.plugin.event.irc.IrcUserProtocolMessageEvent
import java.io.DataOutputStream
import java.net.Socket
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class IrcServer(private val defaultPort: Int) {
    private val outputStreams = HashMap<User, DataOutputStream>()
    private val sockets = HashMap<User, Socket>()
    val connectedUsers = ArrayList<IrcUser>()
    private val registeredCommands = HashMap<String, IrcServerCommand>()

    fun start(port: Int = defaultPort) {
        Thread(IrcServerThread(port)).start()
        Thread(IrcUserTimeoutThread()).start()
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
        IrcUserProtocolMessageEvent(ircUser, command, args).call().then {
            registeredCommands[command.uppercase()]?.handle(ircUser, command, args)
        }
    }

    fun addUser(ircUser: IrcUser, outputStream: DataOutputStream, socket: Socket) {
        IrcUserLoginEvent(ircUser).call()
        connectedUsers.add(ircUser)
        outputStreams[ircUser] = outputStream
        sockets[ircUser] = socket
        Hirasawa.chatEngine.addUser(ircUser)

        sendToUser(ircUser, RplWelcome(Hirasawa.config.ircWelcomeMessage))
        sendToUser(ircUser, RplYourHost())
        sendToUser(ircUser, RplCreated())
        sendToUser(ircUser, RplMyInfo())
        sendToUser(ircUser, RplLUserClient())
        for (line in Hirasawa.config.ircMotd) {
            sendToUser(ircUser, RplMotd(line))
        }
    }

    fun removeUser(ircUser: IrcUser) {
        connectedUsers.remove(ircUser)
        outputStreams[ircUser]?.close()
        sockets[ircUser]?.close()
        outputStreams.remove(ircUser)
        sockets.remove(ircUser)
        Hirasawa.chatEngine.removeUser(ircUser)
    }

    fun registerServerCommand(command: String, ircServerCommand: IrcServerCommand) {
        registeredCommands[command.uppercase()] = ircServerCommand
    }
}