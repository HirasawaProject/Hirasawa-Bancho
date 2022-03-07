package io.hirasawa.server.irc.threads

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.database.tables.UsersTable
import io.hirasawa.server.irc.clientcommands.*
import io.hirasawa.server.irc.objects.IrcUser
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.StringBuilder
import java.net.Socket
import java.util.*

class IrcParserThread(private val socket: Socket) : Runnable {
    private var isLoggedIn = false
    private lateinit var user: IrcUser
    val reader = DataInputStream(BufferedInputStream(socket.getInputStream()))
    val writer = DataOutputStream(socket.getOutputStream())

    override fun run() {
        println("CONNECT")
        val builder = StringBuilder()
        while (socket.isConnected) {
            val temp = reader.readByte().toInt().toChar()
            if (temp == '\n') {
                val args = builder.toString().split(" ")
                val argsWithoutCommand = if (args.size > 1) args.subList(1, args.size).toList().toTypedArray() else arrayOf()
                processCommand(args[0], argsWithoutCommand)
                builder.clear()
            }
            if (temp !in arrayOf('\n', '\r')) {
                print(temp)
                builder.append(temp)
            }
        }
        reader.close()
        writer.close()
        socket.close()
    }

    private fun processCommand(command: String, args: Array<String>) {
        if (isLoggedIn) {
            Hirasawa.irc.handleCommand(user, command, args)
        } else {
            // We're still doing the handshake
            // We're quite basic so we're just gonna handle the USER command as the login
            if (command == "USER") {
                user = IrcUser(transaction {
                    UsersTable.select { UsersTable.username eq args[0] }.first()
                })

                Hirasawa.irc.addUser(user, writer)
                isLoggedIn = true
            }
        }
    }

    private fun processCommand(command: String, writer: DataOutputStream) {
        println("\nCommand: " + command.split(" ")[0])
        val args = command.split(" ")
        when (args[0]) {
            "USER" -> {
//                writer.writeBytes(":127.0.0.1 001 connor :Welcome to Hirasawa\r\n")
//                writer.writeBytes(":127.0.0.1 002 connor :Your host is Hirasawa, running version ${Hirasawa.version}\r\n")
//                writer.writeBytes(":127.0.0.1 003 connor :Your host was created ${Hirasawa.version}\r\n")
//                writer.writeBytes(":127.0.0.1 004 connor 127.0.0.111 Hirasawa o o\r\n")
//                writer.writeBytes(":127.0.0.1 251 connor :There are 2 users and 0 services on 1 server\r\n")
//                writer.writeBytes(":127.0.0.1 375 connor :MOTD HERE\r\n")
//                writer.writeBytes(":127.0.0.1 372 connor :And here\r\n")
//                writer.writeBytes(":127.0.0.1 372 connor :And here 2\r\n")
//                writer.writeBytes(":127.0.0.1 376 connor :END OF MOTD\r\n")
//
//                writer.writeBytes(RplWelcome(user, "Welcome to Hirasawa").generate())
//                writer.writeBytes(RplYourHost(user).generate())
//                writer.writeBytes(RplCreated(user).generate())
//                writer.writeBytes(RplMyInfo(user).generate())
//                writer.writeBytes(RplLUserClient(user).generate())
//                writer.writeBytes(ErrNoMotd(user).generate())
//            }
//            "LIST" -> {
//                writer.writeBytes(RplListStart(user).generate())
//                for (channel in Hirasawa.chatEngine.chatChannels) {
//                    writer.writeBytes(RplList(user, channel.value).generate())
////                    writer.writeBytes(":127.0.0.1 322 connor ${channel.value.name} 532 :${channel.value.description}\r\n")
//                }
//                writer.writeBytes(RplListEnd(user).generate())
////                writer.writeBytes(":127.0.0.1 323 connor :End of LIST\r\n")
//            }
//            "WHOIS" -> {
//                writer.writeBytes(":127.0.0.1 311 connor ${args[1]} ${args[1]} 127.0.0.1 * :Connor Graham\r\n")
//                writer.writeBytes(":127.0.0.1 318 connor ${args[1]} :End of /whois list\r\n")
//            }
//            "JOIN" -> {
//                writer.writeBytes(Join(Hirasawa.chatEngine["#osu"]!!, user).generate())
//                writer.writeBytes(RplTopic(user, Hirasawa.chatEngine["#osu"]!!).generate())
////                writer.writeBytes(RplNoTopic(user, Hirasawa.chatEngine["#osu"]!!).generate())
//                writer.writeBytes(RplNameReply(user, Hirasawa.chatEngine["#osu"]!!, users.toTypedArray()).generate())
//                writer.writeBytes(RplEndOfNames(user, Hirasawa.chatEngine["#osu"]!!).generate())
////                writer.writeBytes(":connor JOIN ${args[1]}\r\n")
//                writer.writeBytes(":127.0.0.1 331 connor ${args[1]} :This is a complete test\r\n")
//                writer.writeBytes(":127.0.0.1 353 connor = ${args[1]} :HirasawaBot connor\r\n")
//                writer.writeBytes(":127.0.0.1 366 connor ${args[1]} :End of NAMES\r\n")
            }
            "PING" -> {
                writer.writeBytes(Pong().generate())
            }
            "PRIVMSG" -> {

                println(":HirasawaBot PRIVMSG #osu ${args.joinToString(" ")}\r\n")
                writer.writeBytes(":HirasawaBot PRIVMSG #osu :${args.joinToString(" ")}\r\n")
                writer.writeBytes(":#osu PRIVMSG #osu :${args.joinToString(" ")}\r\n")
            }

        }
    }
}