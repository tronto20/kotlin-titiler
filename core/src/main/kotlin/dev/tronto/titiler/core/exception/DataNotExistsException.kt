package dev.tronto.titiler.core.exception

open class DataNotExistsException(
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
