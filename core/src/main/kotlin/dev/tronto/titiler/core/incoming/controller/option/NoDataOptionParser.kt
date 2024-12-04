package dev.tronto.titiler.core.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException

class NoDataOptionParser : OptionParser<NoDataOption> {
    companion object {
        const val PARAM = "noData"
    }
    override val type: ArgumentType<NoDataOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): NoDataOption? {
        return request.parameter(PARAM).lastOrNull()?.let {
            val double = it.toDoubleOrNull() ?: throw IllegalParameterException("noData must be a number: $it.")
            NoDataOption(double)
        }
    }

    override fun box(option: NoDataOption): Map<String, List<String>> {
        return mapOf(PARAM to listOf(option.noData.toString()))
    }
}
