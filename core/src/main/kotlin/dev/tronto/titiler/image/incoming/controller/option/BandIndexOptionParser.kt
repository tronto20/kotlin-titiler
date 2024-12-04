package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.domain.BandIndex
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class BandIndexOptionParser : OptionParser<BandIndexOption> {
    companion object {
        const val PARAM = "bidx"
    }
    override val type: ArgumentType<BandIndexOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override suspend fun parse(request: Request): BandIndexOption? {
        val indexes = request.parameter(PARAM).mapNotNull { it.toIntOrNull()?.let { BandIndex(it) } }
        return if (indexes.isNotEmpty()) BandIndexOption(indexes) else null
    }

    override fun box(option: BandIndexOption): Map<String, List<String>> {
        return mapOf(PARAM to option.bandIndexes.map { it.value.toString() })
    }
}
