package dev.tronto.titiler.tile.incoming.controller.option

import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import dev.tronto.titiler.tile.exception.InvalidTileCoordinateException

class TileCoordinateOptionParser : OptionParser<TileCoordinateOption> {
    override val type: ArgumentType<TileCoordinateOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException("x", "y", "z")
    }

    override suspend fun parse(request: Request): TileCoordinateOption? {
        val valueX = request.parameter("x").lastOrNull() ?: return null
        val valueY = request.parameter("y").lastOrNull() ?: return null
        val valueZ = request.parameter("z").lastOrNull() ?: return null
        val x = valueX.toIntOrNull() ?: throw InvalidTileCoordinateException("x", valueX)
        val y = valueY.toIntOrNull() ?: throw InvalidTileCoordinateException("y", valueY)
        val z = valueZ.toIntOrNull() ?: throw InvalidTileCoordinateException("z", valueZ)
        return TileCoordinateOption(z, x, y)
    }

    override fun box(option: TileCoordinateOption): Map<String, List<String>> {
        TODO("Not yet implemented")
    }
}
