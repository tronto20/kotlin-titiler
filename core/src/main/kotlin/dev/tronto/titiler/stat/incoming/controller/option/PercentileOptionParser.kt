package dev.tronto.titiler.stat.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionDescription
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import dev.tronto.titiler.stat.domain.Percentile

class PercentileOptionParser() : OptionParser<PercentileOption> {
    companion object {
        private const val PARAM = "percentile"
    }

    override val type: ArgumentType<PercentileOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): PercentileOption? {
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

    override fun box(option: PercentileOption): Map<String, List<String>> {
        return mapOf(PARAM to option.percentiles.map { it.toString() })
    }

    override fun descriptions(): List<OptionDescription<*>> {
        return listOf(OptionDescription(PARAM, "List of percentile values", arrayOf(2, 98), default = arrayOf(2, 98)))
    }
}
