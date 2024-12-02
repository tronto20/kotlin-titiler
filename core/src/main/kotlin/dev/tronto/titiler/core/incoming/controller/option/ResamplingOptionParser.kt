package dev.tronto.titiler.core.incoming.controller.option

import dev.tronto.titiler.core.domain.ResamplingAlgorithm
import dev.tronto.titiler.core.exception.RequiredParameterMissingException

class ResamplingOptionParser : OptionParser<ResamplingOption> {
    companion object {
        const val PARAM = "resampling"
    }

    override val type: ArgumentType<ResamplingOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override suspend fun parse(request: Request): ResamplingOption? {
        return request.parameter(PARAM).lastOrNull()?.let { ResamplingOption(ResamplingAlgorithm(it)) }
    }
}
