package dev.tronto.titiler.tile.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionDescription
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class TileScaleOptionParser : OptionParser<TileScaleOption> {
    companion object {
        private const val PARAM = "scale"
    }

    override val type: ArgumentType<TileScaleOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): TileScaleOption? {
        return request.parameter(PARAM).firstOrNull()?.let {
            val value = it.toIntOrNull() ?: throw IllegalParameterException("scale must be an integer: $it.")
            TileScaleOption(value)
        }
    }

    override fun box(option: TileScaleOption): Map<String, List<String>> {
        return mapOf(PARAM to listOf(option.scale.toString()))
    }

    override fun descriptions(): List<OptionDescription<*>> {
        return listOf(OptionDescription<Int>(PARAM, "Tile Scale", 1, enums = listOf(1, 2, 3, 4)))
    }
}
