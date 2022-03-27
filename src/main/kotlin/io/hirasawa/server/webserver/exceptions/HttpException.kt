package io.hirasawa.server.webserver.exceptions

import io.hirasawa.server.webserver.enums.HttpStatus

open class HttpException(val httpStatus: HttpStatus): Exception(httpStatus.toString())