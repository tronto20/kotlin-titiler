package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class MaxSizeOptionParser : OptionParser<MaxSizeOption> {
    override val type: ArgumentType<MaxSizeOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return IllegalArgumentException("parameter 'maxSize' is required.")
    }

    override suspend fun parse(request: Request): MaxSizeOption? {
        return request.parameter("maxSize").lastOrNull()?.let {
            val value = it.toIntOrNull() ?: throw IllegalArgumentException("maxSize must be a integer: $it.")
            MaxSizeOption(value)
        }
    }
}
