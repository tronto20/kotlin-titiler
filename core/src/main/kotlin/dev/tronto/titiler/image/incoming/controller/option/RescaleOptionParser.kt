package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class RescaleOptionParser : OptionParser<RescaleOption> {
    override val type: ArgumentType<RescaleOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return IllegalArgumentException("parameter 'rescale' is required.")
    }

    override suspend fun parse(request: Request): RescaleOption? {
        val rescales = request.parameter("rescale").map {
            val (min, max) = it.split(',')
            min.toDouble()..max.toDouble()
        }
        return if (rescales.isNotEmpty()) RescaleOption(rescales) else null
    }
}
