package dev.tronto.titiler.stat.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import dev.tronto.titiler.stat.domain.Percentile

class PercentileOptionParser() : OptionParser<PercentileOption> {
    companion object {
        const val PARAM = "percentile"
    }

    override val type: ArgumentType<PercentileOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override suspend fun parse(request: Request): PercentileOption? {
        val percentiles = request.parameter(PARAM).map {
            it.toIntOrNull() ?: throw IllegalParameterException("$PARAM must be integer.")
        }
        return if (percentiles.isEmpty()) {
            null
        } else {
            PercentileOption(
                percentiles.map {
                    try {
                        Percentile(it)
                    } catch (e: IllegalArgumentException) {
                        throw IllegalParameterException(e.message, e)
                    }
                }
            )
        }
    }
}
