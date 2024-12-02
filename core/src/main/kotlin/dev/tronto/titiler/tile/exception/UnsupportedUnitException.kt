package dev.tronto.titiler.tile.exception

class UnsupportedUnitException(
    val crsString: String,
    val unitName: String,
    cause: Throwable?,
) : UnsupportedOperationException(
    "CRS $crsString with Unit Name `$unitName` is not supported.",
    cause
)
