package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import dev.tronto.titiler.image.domain.Window

class WindowOptionParser : OptionParser<WindowOption> {
    override val type: ArgumentType<WindowOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException("minx", "miny", "maxx", "maxy")
    }

    override suspend fun parse(request: Request): WindowOption? {
        val minxString = request.parameter("minx").lastOrNull() ?: return null
        val maxxString = request.parameter("maxx").lastOrNull() ?: return null
        val minyString = request.parameter("miny").lastOrNull() ?: return null
        val maxyString = request.parameter("maxy").lastOrNull() ?: return null
        val minx = minxString.toIntOrNull() ?: throw IllegalParameterException("minx must be integer: $minxString.")
        val maxx = maxxString.toIntOrNull() ?: throw IllegalParameterException("maxx must be integer: $maxxString.")
        val miny = minyString.toIntOrNull() ?: throw IllegalParameterException("miny must be integer: $minyString.")
        val maxy = maxyString.toIntOrNull() ?: throw IllegalParameterException("maxy must be integer: $maxyString.")

        return WindowOption(
            Window(
                minx,
                miny,
                maxx - minx,
                maxy - miny
            )
        )
    }
}
