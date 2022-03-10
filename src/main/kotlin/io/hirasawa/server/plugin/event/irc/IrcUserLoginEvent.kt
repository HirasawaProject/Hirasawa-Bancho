package io.hirasawa.server.plugin.event.irc

import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

class IrcUserLoginEvent: HirasawaEvent<IrcUserLoginEvent>, Cancelable()