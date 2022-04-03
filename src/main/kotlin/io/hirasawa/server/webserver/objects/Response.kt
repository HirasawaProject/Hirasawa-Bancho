package io.hirasawa.server.webserver.objects

import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.enums.HttpStatus
import kotlinx.html.HTML
import kotlinx.html.dom.create
import kotlinx.html.dom.serialize
import kotlinx.html.html
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.OutputStreamWriter
import javax.xml.parsers.DocumentBuilderFactory

data class Response (var httpStatus: HttpStatus, private val wireOutputStream: DataOutputStream,
                     val headers: MutableHeaders, val cookies: HashMap<String, Cookie>) {
    var isLoggingEnabled = true

    private var haveHeadersBeenSent = false
    private val buffer = ByteArrayOutputStream()
    val outputStream = DataOutputStream(buffer)

    fun writeText(text: String) {
        outputStream.writeBytes(text)
    }

    @Deprecated("Please use the MVC system")
    fun writeRawHtml(block: HTML.() -> Unit) {
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val html = document.create.html{ block() }
        outputStream.writeBytes("<!DOCTYPE html>\n")
        outputStream.writeBytes(html.serialize())
    }

    fun redirect(url: String) {
        httpStatus = HttpStatus.TEMPORARY_REDIRECT
        headers[HttpHeader.LOCATION] = url
    }

    /**
     * Flushes the content of the buffer out onto the web
     * Once a buffer is flushed for the first time headers cannot be modified
     */
    fun flush() {
        if (!haveHeadersBeenSent) {
            // Set version and status
            wireOutputStream.writeBytes("HTTP/1.0 ")
            wireOutputStream.writeBytes(httpStatus.code.toString() + " ")
            wireOutputStream.writeBytes(httpStatus.toString())
            wireOutputStream.writeBytes("\r\n")

            for ((key, value) in headers) {
                wireOutputStream.writeBytes("$key: $value\r\n")
            }

            for ((name, value) in cookies) {
                wireOutputStream.writeBytes("Set-Cookie: $name=${value.encode()}\r\n")
            }

            wireOutputStream.writeBytes("\r\n")
            haveHeadersBeenSent = true
        }

        wireOutputStream.write(buffer.toByteArray())
        wireOutputStream.flush()
        buffer.reset()
    }

    /**
     * Closes the connection to the client and populates the content length header if headers haven't been sent yet
     */
    fun close() {
        if (!haveHeadersBeenSent) {
            headers[HttpHeader.CONTENT_LENGTH] = buffer.size()
        }
        flush()
        wireOutputStream.close()
    }
}