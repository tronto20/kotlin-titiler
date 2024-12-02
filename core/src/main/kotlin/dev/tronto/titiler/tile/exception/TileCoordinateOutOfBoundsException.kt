package dev.tronto.titiler.tile.exception

class TileCoordinateOutOfBoundsException(
    message: String,
) : IndexOutOfBoundsException(message) {
    constructor(
        position: String,
        value: Int,
        range: IntRange,
    ) : this("Tile Coordinate out of bounds. $position must in $range, but $value")

    constructor(
        position: String,
        value: Int,
        values: Iterable<Int>,
    ) : this("Tile Coordinate out of bounds. $position must in $values, but $value")
}
