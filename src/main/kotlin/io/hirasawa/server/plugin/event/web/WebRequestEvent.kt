package io.hirasawa.server.plugin.event.web

import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

/**
 * Event when the webserver is about to respond to a request
 */
class WebRequestEvent(host: String, request: Request, response: Response): HirasawaEvent, Cancelable()