package io.hirasawa.server.irc.clientcommands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.chat.message.ChatMessage
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.irc.enums.NumericReply
import io.hirasawa.server.irc.enums.TextualReply
import io.hirasawa.server.irc.objects.IrcUser
import io.hirasawa.server.webserver.enums.CommonDomains

private val host = CommonDomains.HIRASAWA_IRC.domain
// Textual
class Cap: TextualIrcReply(host, TextualReply.CAP, "* LS")
class Join(chatChannel: ChatChannel, joiningUser: User): TextualIrcReply(joiningUser.username, TextualReply.JOIN, chatChannel.metadata.name)
class Part(chatChannel: ChatChannel, partingUser: User): TextualIrcReply(partingUser.username, TextualReply.PART, chatChannel.metadata.name)
class Pong: TextualIrcReply(host, TextualReply.PONG, "$host :")
class Privmsg(chatMessage: ChatMessage): TextualIrcReply(chatMessage.source.username, TextualReply.PRIVMSG, "${chatMessage.destinationName} :${chatMessage.message}")

// Numeric
class RplWelcome(welcomeMessage: String): NumericIrcReply(host, NumericReply.RPL_WELCOME, welcomeMessage)
class RplYourHost: NumericIrcReply(host, NumericReply.RPL_YOURHOST, "Your host is Hirasawa, running version ${Hirasawa.version}")
class RplCreated: NumericIrcReply(host, NumericReply.RPL_CREATED, "This server was created sometime")
class RplMyInfo: NumericIrcReply(host, NumericReply.RPL_MYINFO, "$host Hirasawa o o")
class RplLUserClient: NumericIrcReply(host, NumericReply.RPL_LUSERCLIENT, ":There are ${Hirasawa.irc.connectedUsers.size + 1} users online")
class RplWhoisUser(context: User): NumericIrcReply(host, NumericReply.RPL_WHOISUSER, "${context.username} ${context.username} 127.0.0.1 *")
class RplEndOfWhois(context: User): NumericIrcReply(host, NumericReply.RPL_ENDOFWHOIS, "${context.username} :End of WHOIS")
class RplListStart: NumericIrcReply(host, NumericReply.RPL_LISTSTART, "Channel :Users Name")
class RplList(chatChannel: ChatChannel): NumericIrcReply(host, NumericReply.RPL_LIST, "${chatChannel.metadata.name} ${chatChannel.size + 1} :${chatChannel.metadata.description}")
class RplListEnd: NumericIrcReply(host, NumericReply.RPL_LISTEND, ":End of LIST")
class RplNoTopic(chatChannel: ChatChannel): NumericIrcReply(host, NumericReply.RPL_NOTOPIC, "${chatChannel.metadata.name} :No topic is set")
class RplTopic(chatChannel: ChatChannel): NumericIrcReply(host, NumericReply.RPL_TOPIC, "${chatChannel.metadata.name} :${chatChannel.metadata.description}")
class RplNameReply(chatChannel: ChatChannel, presence: Array<User>): NumericIrcReply(host, NumericReply.RPL_NAMEREPLY, "= ${chatChannel.metadata.name} :${presence.joinToString(separator = " ") { (if (it.hasPermission("hirasawa.client.admin")) "@" else if (it is IrcUser) "+" else "") + it.username }}")
class RplEndOfNames(chatChannel: ChatChannel): NumericIrcReply(host, NumericReply.RPL_ENDOFNAMES, "${chatChannel.metadata.name} :End of names")
class RplMotdStart: NumericIrcReply(host, NumericReply.RPL_MOTDSTART, ":Hirasawa MOTD")
class RplMotd(motdLine: String): NumericIrcReply(host, NumericReply.RPL_MOTD, ":$motdLine")
class RplEndOfMotd: NumericIrcReply(host, NumericReply.RPL_ENDOFMOTD, ":End of MOTD")
class ErrNoSuchChannel(channelName: String): NumericIrcReply(host, NumericReply.ERR_NOSUCHCHANNEL, "$channelName :No such channel")
class ErrNoMotd: NumericIrcReply(host, NumericReply.ERR_NOMOTD, ":This server has no MOTD")
class ErrPasswdMismatch: NumericIrcReply(host, NumericReply.ERR_PASSWDMISMATCH, ":Bad authentication token")