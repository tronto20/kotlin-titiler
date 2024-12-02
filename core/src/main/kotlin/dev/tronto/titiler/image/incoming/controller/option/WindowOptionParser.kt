package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import dev.tronto.titiler.image.domain.Window

class WindowOptionParser : OptionParser<WindowOption> {
    override val type: ArgumentType<WindowOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return IllegalArgumentException("minx/miny/maxx/maxy parameter required.")
    }

    override suspend fun parse(request: Request): WindowOption? {
        val minxString = request.parameter("minx").lastOrNull() ?: return null
        val maxxString = request.parameter("maxx").lastOrNull() ?: return null
        val minyString = request.parameter("miny").lastOrNull() ?: return null
        val maxyString = request.parameter("maxy").lastOrNull() ?: return null
        val minx = minxString.toIntOrNull() ?: throw IllegalArgumentException("minx must be integer.")
        val maxx = maxxString.toIntOrNull() ?: throw IllegalArgumentException("maxx must be integer.")
        val miny = minyString.toIntOrNull() ?: throw IllegalArgumentException("miny must be integer.")
        val maxy = maxyString.toIntOrNull() ?: throw IllegalArgumentException("maxy must be integer.")

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
