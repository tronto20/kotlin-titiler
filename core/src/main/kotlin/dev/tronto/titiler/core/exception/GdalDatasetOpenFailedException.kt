package dev.tronto.titiler.core.exception

class GdalDatasetOpenFailedException(val path: String, e: Throwable) :
    UnsupportedOperationException("Failed to open dataset $path", e)
