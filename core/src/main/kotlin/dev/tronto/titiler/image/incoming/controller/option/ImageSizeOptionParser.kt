package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class ImageSizeOptionParser : OptionParser<ImageSizeOption> {
    override val type: ArgumentType<ImageSizeOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return IllegalArgumentException("width/height must be provided.")
    }

    override suspend fun parse(request: Request): ImageSizeOption? {
        val widthString = request.parameter("width").lastOrNull() ?: return null
        val heightString = request.parameter("height").lastOrNull() ?: return null
        val width = widthString.toIntOrNull() ?: throw IllegalArgumentException("width must be integer.")
        val height = heightString.toIntOrNull() ?: throw IllegalArgumentException("height must be integer.")
        return ImageSizeOption(width, height)
    }
}
