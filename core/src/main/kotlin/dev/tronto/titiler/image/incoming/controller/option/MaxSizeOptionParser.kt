package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class MaxSizeOptionParser : OptionParser<MaxSizeOption> {
    companion object {
        const val PARAM = "maxSize"
    }
    override val type: ArgumentType<MaxSizeOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): MaxSizeOption? {
        return request.parameter(PARAM).lastOrNull()?.let {
            val value = it.toIntOrNull() ?: throw IllegalParameterException("maxSize must be an integer: $it.")
            MaxSizeOption(value)
        }
    }

    override fun box(option: MaxSizeOption): Map<String, List<String>> {
        return mapOf(PARAM to listOf(option.maxSize.toString()))
    }
}
