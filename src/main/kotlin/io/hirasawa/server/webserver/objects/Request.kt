package io.hirasawa.server.webserver.objects

import io.hirasawa.server.polyfill.readAllBytes
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.handlers.MultipartFormHandler
import io.hirasawa.server.webserver.handlers.ParameterHandler
import java.io.ByteArrayInputStream

data class Request(
    val urlSegment: UrlSegment,
    val httpMethod: HttpMethod,
    val headers: ImmutableHeaders,
    val cookies: HashMap<String, String>,
    val inputStream: ByteArrayInputStream,
    var ipAddress: String
) {
    val routeParameters = HashMap<String, String>()
    val post: HashMap<String, String> by lazy {
        if (headers["content-type"]?.contains("multipart") == true) {
            this.getMultipartForm.elements
        } else {
            ParameterHandler(inputStream.readAllBytes()).parameters
        }
    }

    val files: HashMap<String, ByteArray> by lazy {
        if (headers["content-type"]?.contains("multipart") == true) {
            this.getMultipartForm.files
        } else {
            HashMap()
        }
    }

    val path: String
        get() = urlSegment.route

    val get: HashMap<String, String>
        get() = urlSegment.params

    private val getMultipartForm: MultipartFormHandler by lazy {
        val boundary = Regex("multipart/form-data; boundary=(.+)").matchEntire(headers["content-type"] ?: "")?.groupValues?.get(1) ?: ""
        MultipartFormHandler(inputStream.readAllBytes(), "--$boundary")
    }
}