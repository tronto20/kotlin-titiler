package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class NoDataOptionParser : OptionParser<NoDataOption> {
    override val type: ArgumentType<NoDataOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return IllegalArgumentException("parameter 'noData' is required.")
    }

    override suspend fun parse(request: Request): NoDataOption? {
        return request.parameter("noData").lastOrNull()?.let {
            val double = it.toDoubleOrNull() ?: throw IllegalArgumentException("noData must be a number: $it.")
            NoDataOption(double)
        }
    }
}
