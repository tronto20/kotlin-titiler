package dev.tronto.titiler.wmts.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class MaxZoomOptionParser : OptionParser<MaxZoomOption> {
    companion object {
        const val PARAM = "maxzoom"
    }

    override val type: ArgumentType<MaxZoomOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): MaxZoomOption? {
        return request.parameter(PARAM).lastOrNull()?.let {
            MaxZoomOption(it.toIntOrNull() ?: throw IllegalParameterException("$PARAM must be an integer."))
        }
    }

    override fun box(option: MaxZoomOption): Map<String, List<String>> {
        return mapOf(PARAM to listOf(option.maxZoom.toString()))
    }
}
