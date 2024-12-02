package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class ImageSizeOptionParser : OptionParser<ImageSizeOption> {
    override val type: ArgumentType<ImageSizeOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException("width", "height")
    }

    override suspend fun parse(request: Request): ImageSizeOption? {
        val widthString = request.parameter("width").lastOrNull() ?: return null
        val heightString = request.parameter("height").lastOrNull() ?: return null
        val width = widthString.toIntOrNull() ?: throw IllegalParameterException("width must be integer: $widthString.")
        val height = heightString.toIntOrNull() ?: throw IllegalParameterException(
            "height must be intege: $heightString."
        )
        return ImageSizeOption(width, height)
    }
}
