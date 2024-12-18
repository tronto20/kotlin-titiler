package dev.tronto.titiler.tile.exception

import dev.tronto.titiler.core.exception.DataNotExistsException
import dev.tronto.titiler.tile.incoming.controller.option.TileCoordinateOption

class TileNotFoundException(val coordinate: TileCoordinateOption, cause: Throwable? = null) :
    DataNotExistsException(
        "Tile not found at z=${coordinate.z}, x=${coordinate.x}, y=${coordinate.y}",
        cause
    )
