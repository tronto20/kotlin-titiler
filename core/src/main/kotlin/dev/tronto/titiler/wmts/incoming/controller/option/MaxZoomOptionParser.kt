package dev.tronto.titiler.wmts.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionDescription
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class MaxZoomOptionParser : OptionParser<MaxZoomOption> {
    companion object {
        private const val PARAM = "maxzoom"
    }

    override val type: ArgumentType<MaxZoomOption> = ArgumentType()

    override fun generateMissingException(): Exception = RequiredParameterMissingException(PARAM)

    override suspend fun parse(request: Request): MaxZoomOption? = request.parameter(PARAM).firstOrNull()?.let {
        MaxZoomOption(it.toIntOrNull() ?: throw IllegalParameterException("$PARAM must be an integer."))
    }

    override fun box(option: MaxZoomOption): Map<String, List<String>> =
        mapOf(PARAM to listOf(option.maxZoom.toString()))

    override fun descriptions(): List<OptionDescription<*>> =
        listOf(OptionDescription<Int>(PARAM, "Overwrite default maxzoom.", 20))
}
