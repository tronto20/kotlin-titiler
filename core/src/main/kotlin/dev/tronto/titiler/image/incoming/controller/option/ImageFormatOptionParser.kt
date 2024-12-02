package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import dev.tronto.titiler.image.domain.ImageFormat

class ImageFormatOptionParser : OptionParser<ImageFormatOption> {
    override val type: ArgumentType<ImageFormatOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return IllegalArgumentException("parameter 'format' is required.")
    }

    override suspend fun parse(request: Request): ImageFormatOption? {
        return request.parameter("format").lastOrNull()?.let {
            if (it.equals("auto", true)) {
                ImageFormatOption(null)
            } else {
                ImageFormatOption(ImageFormat(it))
            }
        }
    }
}
