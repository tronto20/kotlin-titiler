package dev.tronto.titiler.tile.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class TileMatrixSetOptionParser : OptionParser<TileMatrixSetOption> {
    companion object {
        const val PARAM = "tileMatrixSetId"
    }

    override val type: ArgumentType<TileMatrixSetOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return IllegalParameterException(PARAM)
    }

    override suspend fun parse(request: Request): TileMatrixSetOption? {
        return request.parameter(PARAM).lastOrNull()?.let {
            TileMatrixSetOption(it)
        }
    }
}
