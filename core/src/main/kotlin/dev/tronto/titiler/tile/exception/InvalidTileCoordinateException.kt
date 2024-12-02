package dev.tronto.titiler.tile.exception

import dev.tronto.titiler.core.exception.IllegalParameterException

class InvalidTileCoordinateException(
    val position: String,
    val value: String,
) : IllegalParameterException(
    "Invalid tile coordinate. Coordinate must be integer type. $position: $value"
)
