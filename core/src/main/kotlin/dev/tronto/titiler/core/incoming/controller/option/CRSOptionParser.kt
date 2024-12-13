package dev.tronto.titiler.core.incoming.controller.option

import dev.tronto.titiler.core.exception.RequiredParameterMissingException

class CRSOptionParser : OptionParser<CRSOption> {
    companion object {
        private const val PARAM = "dstCrs"
    }

    override val type: ArgumentType<CRSOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): CRSOption? {
        return request.parameter(PARAM).firstOrNull()?.let { CRSOption(it) }
    }

    override fun box(option: CRSOption): Map<String, List<String>> {
        return mapOf(PARAM to listOf(option.crsString))
    }

    override fun descriptions(): List<OptionDescription<*>> {
        return listOf(
            OptionDescription<String>(PARAM, "target crs.", "EPSG:4326")
        )
    }
}
