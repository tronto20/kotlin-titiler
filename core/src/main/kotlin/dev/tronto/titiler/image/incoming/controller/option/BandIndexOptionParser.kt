package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.domain.BandIndex
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class BandIndexOptionParser : OptionParser<BandIndexOption> {
    override val type: ArgumentType<BandIndexOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return IllegalArgumentException("parameter 'bidx' is required.")
    }

    override suspend fun parse(request: Request): BandIndexOption? {
        val indexes = request.parameter("bidx").mapNotNull { it.toIntOrNull()?.let { BandIndex(it) } }
        return if (indexes.isNotEmpty()) BandIndexOption(indexes) else null
    }
}
