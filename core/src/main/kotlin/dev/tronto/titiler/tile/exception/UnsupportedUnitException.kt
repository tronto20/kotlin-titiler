package dev.tronto.titiler.tile.exception

import dev.tronto.titiler.core.exception.IllegalParameterException

class UnsupportedUnitException(
    val crsString: String,
    val unitName: String,
    cause: Throwable?,
) : IllegalParameterException(
    "CRS $crsString with Unit Name `$unitName` is not supported.",
    cause
)
