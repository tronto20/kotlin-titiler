package dev.tronto.titiler.tile.exception

import dev.tronto.titiler.core.exception.IllegalParameterException

class UnsupportedTileMatrixSetException(id: String) :
    IllegalParameterException(
        "TileMatrixSet $id is not supported."
    )
