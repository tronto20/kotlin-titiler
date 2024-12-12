package dev.tronto.titiler.tile.incoming.controller.option

import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionDescription
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import dev.tronto.titiler.tile.exception.InvalidTileCoordinateException

class TileCoordinateOptionParser : OptionParser<TileCoordinateOption> {
    override val type: ArgumentType<TileCoordinateOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException("x", "y", "z")
    }

    override fun parse(request: Request): TileCoordinateOption? {
        val valueX = request.parameter("x").firstOrNull() ?: return null
        val valueY = request.parameter("y").firstOrNull() ?: return null
        val valueZ = request.parameter("z").firstOrNull() ?: return null
        val x = valueX.toIntOrNull() ?: throw InvalidTileCoordinateException("x", valueX)
        val y = valueY.toIntOrNull() ?: throw InvalidTileCoordinateException("y", valueY)
        val z = valueZ.toIntOrNull() ?: throw InvalidTileCoordinateException("z", valueZ)
        return TileCoordinateOption(z, x, y)
    }

    override fun box(option: TileCoordinateOption): Map<String, List<String>> {
        return mapOf(
            "x" to listOf(option.x.toString()),
            "y" to listOf(option.y.toString()),
            "z" to listOf(option.z.toString())
        )
    }

    override fun descriptions(): List<OptionDescription<*>> {
        return listOf(
            OptionDescription<Int>(
                "z",
                "Identifier (Z) selecting one of the scales defined " +
                    "in the TileMatrixSet and representing the scaleDenominator the tile."
            ),
            OptionDescription<Int>(
                "x",
                "Column (X) index of the tile on the selected TileMatrix. " +
                    "It cannot exceed the MatrixHeight-1 for the selected TileMatrix."
            ),
            OptionDescription<Int>(
                "y",
                "Row (Y) index of the tile on the selected TileMatrix. " +
                    "It cannot exceed the MatrixWidth-1 for the selected TileMatrix."
            )
        )
    }
}
