package io.hirasawa.server.plugin.event.irc

import io.hirasawa.server.irc.objects.IrcUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

class IrcUserHandshakeSuccessEvent(user: IrcUser): HirasawaEvent<IrcUserHandshakeSuccessEvent>, Cancelable()