package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request

class ImageSizeOptionParser : OptionParser<ImageSizeOption> {
    companion object {
        private const val WIDTH = "width"
        private const val HEIGHT = "height"
    }
    override val type: ArgumentType<ImageSizeOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(WIDTH, HEIGHT)
    }

    override fun parse(request: Request): ImageSizeOption? {
        val widthString = request.parameter(WIDTH).firstOrNull() ?: return null
        val heightString = request.parameter(HEIGHT).firstOrNull() ?: return null
        val width = widthString.toIntOrNull() ?: throw IllegalParameterException(
            "$WIDTH must be an integer: $widthString."
        )
        val height = heightString.toIntOrNull() ?: throw IllegalParameterException(
            "$HEIGHT must be an integer: $heightString."
        )
        return ImageSizeOption(width, height)
    }

    override fun box(option: ImageSizeOption): Map<String, List<String>> {
        return mapOf(
            WIDTH to listOf(option.width.toString()),
            HEIGHT to listOf(option.height.toString())
        )
    }
}
