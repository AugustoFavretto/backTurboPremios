package com.turbopremios.exceptions

import org.springframework.http.HttpStatus

open class AppException(
    val status: HttpStatus,
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class NotFoundException(message: String) : AppException(HttpStatus.NOT_FOUND, message)
class ConflictException(message: String) : AppException(HttpStatus.CONFLICT, message)
class BadRequestException(message: String) : AppException(HttpStatus.BAD_REQUEST, message)
class UnauthorizedException(message: String) : AppException(HttpStatus.UNAUTHORIZED, message)
class ForbiddenException(message: String) : AppException(HttpStatus.FORBIDDEN, message)
class UnprocessableEntityException(message: String) : AppException(HttpStatus.UNPROCESSABLE_ENTITY, message)
