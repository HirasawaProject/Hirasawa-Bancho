package io.hirasawa.server.webserver.objects

import io.hirasawa.server.webserver.enums.HttpStatus
import kotlinx.html.HTML
import kotlinx.html.dom.create
import kotlinx.html.dom.serialize
import kotlinx.html.html
import java.io.DataOutputStream
import javax.xml.parsers.DocumentBuilderFactory

data class Response (var httpStatus: HttpStatus, val outputStream: DataOutputStream,
                     val headers: MutableHeaders) {
    var isLoggingEnabled = true
    fun writeText(text: String) {
        outputStream.writeBytes(text)
    }

    fun writeRawHtml(block: HTML.() -> Unit) {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val html = document.create.html{ block() }
        outputStream.writeBytes("<!DOCTYPE html>\n")
        outputStream.writeBytes(html.serialize())
    }
}