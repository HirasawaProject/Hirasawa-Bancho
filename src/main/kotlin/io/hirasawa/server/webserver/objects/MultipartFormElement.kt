package io.hirasawa.server.webserver.objects

data class MultipartFormElement(val name: String, val headers: ImmutableHeaders, val content: List<Byte>)