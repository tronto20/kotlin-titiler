package dev.tronto.titiler.wmts.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionDescription
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class MinZoomOptionParser : OptionParser<MinZoomOption> {
    companion object {
        private const val PARAM = "minzoom"
    }

    override val type: ArgumentType<MinZoomOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): MinZoomOption? {
        return request.parameter(PARAM).firstOrNull()?.let {
            MinZoomOption(it.toIntOrNull() ?: throw IllegalParameterException("$PARAM must be an integer."))
        }
    }

    override fun box(option: MinZoomOption): Map<String, List<String>> {
        return mapOf(PARAM to listOf(option.minZoom.toString()))
    }

    override fun descriptions(): List<OptionDescription<*>> {
        return listOf(OptionDescription<Int>(PARAM, "Overwrite default minzoom.", 0))
    }
}
