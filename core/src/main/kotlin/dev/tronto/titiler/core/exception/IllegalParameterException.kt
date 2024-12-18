package dev.tronto.titiler.core.exception

open class IllegalParameterException(message: String? = null, cause: Throwable? = null) :
    IllegalArgumentException(
        message,
        cause
    )
