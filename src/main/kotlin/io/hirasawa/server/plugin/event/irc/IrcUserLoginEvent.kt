package io.hirasawa.server.plugin.event.irc

import io.hirasawa.server.irc.objects.IrcUser
import io.hirasawa.server.plugin.event.HirasawaEvent

class IrcUserLoginEvent(user: IrcUser): HirasawaEvent<IrcUserLoginEvent>