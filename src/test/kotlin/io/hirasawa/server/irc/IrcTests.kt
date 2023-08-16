package io.hirasawa.server.irc

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.plugin.internalplugins.InternalBanchoPlugin
import io.hirasawa.server.plugin.internalplugins.InternalIrcPlugin
import io.hirasawa.server.webserver.Helper.Companion.createUser
import io.hirasawa.server.webserver.enums.CommonDomains
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.*
import java.net.Socket
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IrcTests {
    init {
        Hirasawa.initDatabase(true)
        Hirasawa.pluginManager.loadPlugin(InternalIrcPlugin(7000), InternalIrcPlugin.descriptor)
        Hirasawa.pluginManager.loadPlugin(InternalBanchoPlugin(), InternalBanchoPlugin.descriptor)
    }
    private val host = CommonDomains.HIRASAWA_IRC
    private val version = Hirasawa.version

    private fun connectToIrc(username: String, ircToken: String): Triple<Socket, DataOutputStream, Scanner> {
        val socket = Socket("localhost", 7000)
        val writer = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
        val reader = Scanner(BufferedInputStream(socket.getInputStream()))

        writer.writeBytes("NICK $username\r\n")
        writer.writeBytes("PASS $ircToken\r\n")
        writer.writeBytes("USER $username\r\n")
        writer.flush()

        return Triple(socket, writer, reader)
    }

    private fun Scanner.skipMotd() {
        while (this.hasNext()) {
            if (this.nextLine().contains(":set this to whatever you want")) break
        }
    }

    private fun DataOutputStream.writeBytesAndFlush(message: String) {
        this.writeBytes(message)
        this.flush()
    }

    @Test
    fun testCanJoinAndLeaveIrc() {
        createUser("TCJALI")
        val (socket, writer, reader) = connectToIrc("TCJALI", "ircToken")

        assertEquals(":$host 001 TCJALI Welcome to Hirasawa!", reader.nextLine())
        assertEquals(":$host 002 TCJALI Your host is Hirasawa, running version $version", reader.nextLine())
        assertEquals(":$host 003 TCJALI This server was created sometime", reader.nextLine())
        assertEquals(":$host 004 TCJALI $host Hirasawa o o", reader.nextLine())
        assertEquals(":$host 251 TCJALI :There are 2 users online", reader.nextLine())
        assertEquals(":$host 372 TCJALI :This is an example of a MOTD for the IRC server", reader.nextLine())
        assertEquals(":$host 372 TCJALI :set this to whatever you want", reader.nextLine())

        assertEquals(1, Hirasawa.irc.connectedUsers.size)

        writer.writeBytesAndFlush("QUIT\r\n")

        Thread.sleep(1000)

        assertEquals(0, Hirasawa.irc.connectedUsers.size)
        assertTrue(socket.getInputStream().read() == -1) // Basic check to see if socket is closed
        socket.close()
    }

    @Test
    fun testCanJoinAndLeaveIrcWithColonToken() {
        createUser("TCJALI")
        val (socket, writer, reader) = connectToIrc("TCJALI", ":ircToken")

        assertEquals(":$host 001 TCJALI Welcome to Hirasawa!", reader.nextLine())
        assertEquals(":$host 002 TCJALI Your host is Hirasawa, running version $version", reader.nextLine())
        assertEquals(":$host 003 TCJALI This server was created sometime", reader.nextLine())
        assertEquals(":$host 004 TCJALI $host Hirasawa o o", reader.nextLine())
        assertEquals(":$host 251 TCJALI :There are 2 users online", reader.nextLine())
        assertEquals(":$host 372 TCJALI :This is an example of a MOTD for the IRC server", reader.nextLine())
        assertEquals(":$host 372 TCJALI :set this to whatever you want", reader.nextLine())

        assertEquals(1, Hirasawa.irc.connectedUsers.size)

        writer.writeBytesAndFlush("QUIT\r\n")

        Thread.sleep(1000)

        assertEquals(0, Hirasawa.irc.connectedUsers.size)
        assertTrue(socket.getInputStream().read() == -1) // Basic check to see if socket is closed
        socket.close()
    }

    @Test
    fun testWillGetRejectedForWrongPassword() {
        createUser("TWGRFWP")
        val (socket, _, reader) = connectToIrc("TWGRFWP", "Incorrect token")
        assertEquals(":$host 372 TWGRFWP :Welcome to Hirasawa!", reader.nextLine())
        assertEquals(":$host 372 TWGRFWP :-", reader.nextLine())
        assertEquals(":$host 372 TWGRFWP :- You are required to authenticate before accessing this server", reader.nextLine())
        assertEquals(":$host 372 TWGRFWP :- Run !ircsetup in osu! in order to see your authentication token for IRC", reader.nextLine())
        assertEquals(":$host 464 TWGRFWP :Bad authentication token", reader.nextLine())

        Thread.sleep(1000)

        assertEquals(0, Hirasawa.irc.connectedUsers.size)
        assertTrue(socket.getInputStream().read() == -1) // Basic check to see if socket is closed
        socket.close()
    }

    @Test
    fun testWillGetRejectedForWrongUsername() {
        val (socket, _, reader) = connectToIrc("incorrect_username", "Incorrect token")
        assertEquals(":$host 372 incorrect_username :Welcome to Hirasawa!", reader.nextLine())
        assertEquals(":$host 372 incorrect_username :-", reader.nextLine())
        assertEquals(":$host 372 incorrect_username :- You are required to authenticate before accessing this server", reader.nextLine())
        assertEquals(":$host 372 incorrect_username :- Run !ircsetup in osu! in order to see your authentication token for IRC", reader.nextLine())
        assertEquals(":$host 464 incorrect_username :Bad authentication token", reader.nextLine())

        Thread.sleep(1000)

        assertEquals(0, Hirasawa.irc.connectedUsers.size)
        assertTrue(socket.getInputStream().read() == -1) // Basic check to see if socket is closed
        socket.close()
    }

    @Test
    fun testCanJoinChannelAndRunCommand() {
        createUser("TCJCARC")
        val (socket, writer, reader) = connectToIrc("TCJCARC", "ircToken")
        reader.skipMotd()

        writer.writeBytesAndFlush("JOIN #osu\r\n")

        assertEquals(":TCJCARC JOIN #osu", reader.nextLine())
        assertEquals(":$host 332 TCJCARC #osu :Main channel", reader.nextLine())
        assertEquals(":$host 353 TCJCARC = #osu :TCJCARC", reader.nextLine())
        assertEquals(":$host 353 TCJCARC = #osu :BanchoBot", reader.nextLine())
        assertEquals(":$host 366 TCJCARC #osu :End of names", reader.nextLine())

        writer.writeBytesAndFlush("PRIVMSG #osu :!ping\r\n")
        assertEquals(":BanchoBot PRIVMSG #osu :Pong!", reader.nextLine())

        writer.writeBytesAndFlush("QUIT\r\n")
        assertTrue(socket.getInputStream().read() == -1) // Basic check to see if socket is closed
        socket.close()
        Thread.sleep(1000)
    }

    @Test
    fun testCanJoinChannelAndChatBetweenTwoClients() {
        createUser("TCJCACBTC1")
        createUser("TCJCACBTC2")
        // Client 1
        val (socket1, writer1, reader1) = connectToIrc("TCJCACBTC1", "ircToken")
        reader1.skipMotd()

        writer1.writeBytesAndFlush("JOIN #osu\r\n")

        // Confirm user1 joined #osu
        assertEquals(":TCJCACBTC1 JOIN #osu", reader1.nextLine())
        assertEquals(":$host 332 TCJCACBTC1 #osu :Main channel", reader1.nextLine())
        assertEquals(":$host 353 TCJCACBTC1 = #osu :TCJCACBTC1", reader1.nextLine())
        assertEquals(":$host 353 TCJCACBTC1 = #osu :BanchoBot", reader1.nextLine())
        assertEquals(":$host 366 TCJCACBTC1 #osu :End of names", reader1.nextLine())

        // Client 2
        val (socket2, writer2, reader2) = connectToIrc("TCJCACBTC2", "ircToken")
        reader2.skipMotd()

        writer2.writeBytesAndFlush("JOIN #osu\r\n")

        // Confirm user2 joined #osu
        assertEquals(":TCJCACBTC2 JOIN #osu", reader2.nextLine())
        assertEquals(":$host 332 TCJCACBTC2 #osu :Main channel", reader2.nextLine())
        assertEquals(":$host 353 TCJCACBTC2 = #osu :TCJCACBTC1 TCJCACBTC2", reader2.nextLine())
        assertEquals(":$host 353 TCJCACBTC2 = #osu :BanchoBot", reader2.nextLine())
        assertEquals(":$host 366 TCJCACBTC2 #osu :End of names", reader2.nextLine())

        // Confirm user1 knows user2 joined #osu
        assertEquals(":TCJCACBTC2 JOIN #osu", reader1.nextLine())

        // Client 1 sends message in #osu
        writer1.writeBytesAndFlush("PRIVMSG #osu :This is a test\r\n")

        // Confirm client 2 got message
        assertEquals(":TCJCACBTC1 PRIVMSG #osu :This is a test", reader2.nextLine())

        // Have client 1 leave #osu
        writer1.writeBytesAndFlush("PART #osu\r\n")

        // Check client2 saw them leave
        assertEquals(":TCJCACBTC1 PART #osu", reader2.nextLine())

        // Check client1 was confirmed to leave
        assertEquals(":TCJCACBTC1 PART #osu", reader1.nextLine())

        writer1.writeBytesAndFlush("QUIT\r\n")
        writer2.writeBytesAndFlush("QUIT\r\n")

        Thread.sleep(1000)
        assertTrue(socket1.getInputStream().read() == -1) // Basic check to see if socket is closed
        assertTrue(socket2.getInputStream().read() == -1) // Basic check to see if socket is closed
        socket1.close()
        socket2.close()
    }

    @Test
    fun testCanPrivateMessageBetweenTwoClients() {
        createUser("TCPMBTC1")
        createUser("TCPMBTC2")
        // Client 1
        val (socket1, writer1, reader1) = connectToIrc("TCPMBTC1", "ircToken")
        reader1.skipMotd()

        // Client 2
        val (socket2, writer2, reader2) = connectToIrc("TCPMBTC2", "ircToken")
        reader2.skipMotd()

        // Client 1 sends message in #osu
        writer1.writeBytesAndFlush("PRIVMSG TCPMBTC2 :This is a test\r\n")

        // Confirm client 2 got message
        assertEquals(":TCPMBTC1 PRIVMSG TCPMBTC2 :This is a test", reader2.nextLine())

        writer1.writeBytesAndFlush("QUIT\r\n")
        writer2.writeBytesAndFlush("QUIT\r\n")

        assertTrue(socket1.getInputStream().read() == -1) // Basic checj to see if socket is closed
        assertTrue(socket2.getInputStream().read() == -1) // Basic check to see if socket is closed
        socket1.close()
        socket2.close()

        Thread.sleep(1000)
    }

    @Test
    fun canClientListChannels() {
        createUser("CCLC")
        val (socket, writer, reader) = connectToIrc("CCLC", "ircToken")
        reader.skipMotd()
        writer.writeBytesAndFlush("LIST\r\n")
        assertEquals(":$host 321 CCLC Channel :Users Name", reader.nextLine())
        val channelLines = arrayListOf(
            reader.nextLine(),
            reader.nextLine(),
        )
        // We can't guarantee the order
        assert(":$host 322 CCLC #osu 1 :Main channel" in channelLines)
        assert(":$host 322 CCLC #lounge 1 :Administration channel" in channelLines)
        assertEquals(":$host 323 CCLC :End of LIST", reader.nextLine())

        writer.writeBytesAndFlush("QUIT\r\n")
        assertTrue(socket.getInputStream().read() == -1) // Basic check to see if socket is closed
        socket.close()
    }
}