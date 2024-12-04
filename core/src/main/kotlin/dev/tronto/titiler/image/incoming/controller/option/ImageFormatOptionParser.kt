package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import dev.tronto.titiler.image.domain.ImageFormat

class ImageFormatOptionParser : OptionParser<ImageFormatOption> {
    companion object {
        const val PARAM = "format"
        const val AUTO = "auto"
    }

    override val type: ArgumentType<ImageFormatOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): ImageFormatOption? {
        return request.parameter(PARAM).lastOrNull()?.let {
            if (it.equals(AUTO, true)) {
                ImageFormatOption(null)
            } else {
                try {
                    ImageFormatOption(ImageFormat(it))
                } catch (e: IllegalArgumentException) {
                    throw IllegalParameterException(e.message, e)
                }
            }
        }
    }

    override fun box(option: ImageFormatOption): Map<String, List<String>> {
        return mapOf(PARAM to listOf(option.format?.name?.lowercase() ?: AUTO))
    }
}
