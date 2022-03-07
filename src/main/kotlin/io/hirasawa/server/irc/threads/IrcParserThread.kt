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
import java.lang.Exception
import java.lang.StringBuilder
import java.net.Socket
import java.util.*

class IrcParserThread(private val socket: Socket) : Runnable {
    private var isLoggedIn = false
    private var password = ""
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
            print(temp)
            if (temp !in arrayOf('\n', '\r')) {
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
            try {
                when (command) {
                    "USER" -> {
                        if (password == "") {
                            writer.writeBytes(ErrPasswdMismatch().generate(args[0]))
                            socket.close()
                            return
                        }

                        if (!Hirasawa.authenticateIrc(args[0], password)) {
                            writer.writeBytes(ErrPasswdMismatch().generate(args[0]))
                            socket.close()
                            return
                        }
                        user = IrcUser(transaction {
                            UsersTable.select { UsersTable.username eq args[0] }.first()
                        })

                        Hirasawa.irc.addUser(user, writer)
                        isLoggedIn = true
                    }
                    "PASS" -> {
                        password = args[0]
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }

        }
    }
}