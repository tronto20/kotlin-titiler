package dev.tronto.titiler.core.exception

class InvalidURIException(
    val uriString: String,
    cause: Throwable? = null,
) : IllegalParameterException("Invalid URI : $uriString", cause)
