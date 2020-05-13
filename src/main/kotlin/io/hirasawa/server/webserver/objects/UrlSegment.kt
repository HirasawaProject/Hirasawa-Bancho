package io.hirasawa.server.webserver.objects

data class UrlSegment(val host: String, val route: String, val params: HashMap<String, String>)