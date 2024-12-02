package dev.tronto.titiler.tile.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class TileScaleOptionParser : OptionParser<TileScaleOption> {
    companion object {
        const val PARAM = "scale"
    }

    override val type: ArgumentType<TileScaleOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override suspend fun parse(request: Request): TileScaleOption? {
        return request.parameter(PARAM).lastOrNull()?.let {
            val value = it.toIntOrNull() ?: throw IllegalParameterException("scale must be a integer: $it.")
            TileScaleOption(value)
        }
    }
}
