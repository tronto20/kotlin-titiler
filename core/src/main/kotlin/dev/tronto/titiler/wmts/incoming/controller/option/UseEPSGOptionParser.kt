package dev.tronto.titiler.wmts.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class UseEPSGOptionParser : OptionParser<UseEPSGOption> {
    companion object {
        private const val PARAM = "useEpsg"
    }
    override val type: ArgumentType<UseEPSGOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): UseEPSGOption? {
        return request.parameter(PARAM).firstOrNull()?.let {
            it.lowercase().toBooleanStrictOrNull() ?: throw IllegalParameterException("$PARAM must be a boolean.")
        }?.let {
            UseEPSGOption(it)
        }
    }

    override fun box(option: UseEPSGOption): Map<String, List<String>> {
        return mapOf(PARAM to listOf(option.useEpsg.toString()))
    }
}
