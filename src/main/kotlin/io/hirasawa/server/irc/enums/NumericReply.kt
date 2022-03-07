package io.hirasawa.server.irc.enums

enum class NumericReply(val id: String) {
    RPL_WELCOME("001"),
    RPL_YOURHOST("002"),
    RPL_CREATED("003"),
    RPL_MYINFO("004"),

    RPL_LUSERCLIENT("251"),

    RPL_WHOISUSER("311"),
    RPL_ENDOFWHOIS("318"),

    RPL_LISTSTART("321"),
    RPL_LIST("322"),
    RPL_LISTEND("323"),

    RPL_NOTOPIC("331"),
    RPL_TOPIC("332"),

    RPL_NAMEREPLY("353"),
    RPL_ENDOFNAMES("366"),

    RPL_MOTDSTART("375"),
    RPL_MOTD("372"),
    RPL_ENDOFMOTD("376"),

    ERR_NOSUCHCHANNEL("403"),

    ERR_NOMOTD("422"),

    ERR_PASSWDMISMATCH("464"),
}