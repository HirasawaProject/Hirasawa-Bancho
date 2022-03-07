package io.hirasawa.server.irc.clientcommands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.bancho.chat.message.ChatMessage
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.irc.enums.NumericReply
import io.hirasawa.server.irc.enums.TextualReply
import io.hirasawa.server.webserver.enums.CommonDomains

private val host = CommonDomains.HIRASAWA_IRC.domain
// Textual
class Join(chatChannel: ChatChannel, joiningUser: User): TextualIrcReply(joiningUser.username, TextualReply.JOIN, chatChannel.name)
class Pong: TextualIrcReply(host, TextualReply.PONG, "$host :")
class Privmsg(chatMessage: ChatMessage): TextualIrcReply(chatMessage.source.username, TextualReply.PRIVMSG, "${chatMessage.destinationName} :${chatMessage.message}")

// Numeric
class RplWelcome(user: User, welcomeMessage: String): NumericIrcReply(host, NumericReply.RPL_WELCOME, user.username, welcomeMessage)
class RplYourHost(user: User): NumericIrcReply(host, NumericReply.RPL_YOURHOST, user.username, "Your host is Hirasawa, running version ${Hirasawa.version}")
class RplCreated(user: User): NumericIrcReply(host, NumericReply.RPL_CREATED, user.username, "This server was created sometime")
class RplMyInfo(user: User): NumericIrcReply(host, NumericReply.RPL_MYINFO, user.username, "$host Hirasawa o o")
class RplLUserClient(user: User): NumericIrcReply(host, NumericReply.RPL_LUSERCLIENT, user.username, ":There are 2 users 0 invisible on 1 servers")
class RplWhoisUser(to: User, context: User): NumericIrcReply(host, NumericReply.RPL_WHOISUSER, to.username, "${context.username} ${context.username} 127.0.0.1 *")
class RplEndOfWhois(to: User, context: User): NumericIrcReply(host, NumericReply.RPL_ENDOFWHOIS, to.username, "${context.username} :End of WHOIS")
class RplListStart(user: User): NumericIrcReply(host, NumericReply.RPL_LISTSTART, user.username, "Channel :Users Name")
class RplList(to: User, chatChannel: ChatChannel): NumericIrcReply(host, NumericReply.RPL_LIST, to.username, "${chatChannel.name} ${chatChannel.size + 1} :${chatChannel.description}")
class RplListEnd(to: User): NumericIrcReply(host, NumericReply.RPL_LISTEND, to.username, ":End of LIST")
class RplNoTopic(to: User, chatChannel: ChatChannel): NumericIrcReply(host, NumericReply.RPL_NOTOPIC, to.username, "${chatChannel.name} :No topic is set")
class RplTopic(to: User, chatChannel: ChatChannel): NumericIrcReply(host, NumericReply.RPL_TOPIC, to.username, "${chatChannel.name} :${chatChannel.description}")
class RplNameReply(to: User, chatChannel: ChatChannel, presence: Array<User>): NumericIrcReply(host, NumericReply.RPL_NAMEREPLY, to.username, "= ${chatChannel.name} :${presence.joinToString(separator = " ") { it.username }}")
class RplEndOfNames(to: User, chatChannel: ChatChannel): NumericIrcReply(host, NumericReply.RPL_ENDOFNAMES, to.username, "${chatChannel.name} :End of names")
class RplMotdStart(user: User, motdLine: String): NumericIrcReply(host, NumericReply.RPL_MOTDSTART, user.username, ":$motdLine")
class RplMotd(user: User, motdLine: String): NumericIrcReply(host, NumericReply.RPL_MOTD, user.username, ":$motdLine")
class RplEndOfMotd(user: User, motdLine: String): NumericIrcReply(host, NumericReply.RPL_ENDOFMOTD, user.username, ":$motdLine")
class ErrNoSuchChannel(user: User, channelName: String): NumericIrcReply(host, NumericReply.ERR_NOSUCHCHANNEL, user.username, "$channelName :No such channel")
class ErrNoMotd(user: User): NumericIrcReply(host, NumericReply.ERR_NOMOTD, user.username, ":This server has no MOTD")