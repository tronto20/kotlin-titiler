package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionDescription
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class MaxSizeOptionParser : OptionParser<MaxSizeOption> {
    companion object {
        private const val PARAM = "maxSize"
    }

    override val type: ArgumentType<MaxSizeOption> = ArgumentType()

    override fun generateMissingException(): Exception = RequiredParameterMissingException(PARAM)

    override suspend fun parse(request: Request): MaxSizeOption? = request.parameter(PARAM).firstOrNull()?.let {
        val value = it.toIntOrNull() ?: throw IllegalParameterException("maxSize must be an integer: $it.")
        MaxSizeOption(value)
    }

    override fun box(option: MaxSizeOption): Map<String, List<String>> =
        mapOf(PARAM to listOf(option.maxSize.toString()))

    override fun descriptions(): List<OptionDescription<*>> =
        listOf(OptionDescription<Int>(PARAM, "image max size", default = 1024, sample = 1024))
}
