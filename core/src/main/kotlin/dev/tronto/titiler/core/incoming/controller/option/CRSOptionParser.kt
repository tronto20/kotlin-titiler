package dev.tronto.titiler.core.incoming.controller.option

import dev.tronto.titiler.core.exception.RequiredParameterMissingException

class CRSOptionParser : OptionParser<CRSOption> {
    companion object {
        const val PARAM = "dstCrs"
    }

    override val type: ArgumentType<CRSOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override suspend fun parse(request: Request): CRSOption? {
        return request.parameter(PARAM).lastOrNull()?.let { CRSOption(it) }
    }
}
