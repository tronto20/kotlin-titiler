package dev.tronto.titiler.tile.exception

class UnsupportedTileMatrixSetException(id: String) : UnsupportedOperationException(
    "TileMatrixSet $id is not supported."
)
