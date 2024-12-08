package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.domain.Ordered
import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import dev.tronto.titiler.image.domain.ImageFormat
import dev.tronto.titiler.image.spi.ImageFormatRegistrar
import java.util.*

class ImageFormatOptionParser(
    imageFormats: Iterable<ImageFormat>,
) : OptionParser<ImageFormatOption> {

    constructor() : this(
        ServiceLoader.load(ImageFormatRegistrar::class.java, Thread.currentThread().contextClassLoader)
            .flatMap { it.imageFormats() }
            .sortedBy { if (it is Ordered) it.order else 0 }.toList()
    )

    companion object {
        private const val CONTENT_TYPE = "content-type"
        private const val PARAM = "format"
    }

    private val imageFormatMap = imageFormats
        .flatMap { format -> (listOf(format.name, *format.aliasNames.toTypedArray())).map { it to format } }
        .toMap()

    override val type: ArgumentType<ImageFormatOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): ImageFormatOption? {
        request.option(CONTENT_TYPE).firstOrNull()?.let {
            return createOption(CONTENT_TYPE, it)
        }
        request.parameter(PARAM).firstOrNull()?.let {
            return createOption(PARAM, it)
        }
        return null
    }

    private fun createOption(name: String, value: String): ImageFormatOption {
        try {
            val format = imageFormatMap[value]
                ?: throw IllegalParameterException("$name must be one of ${imageFormatMap.keys}")
            return ImageFormatOption(format)
        } catch (e: IllegalArgumentException) {
            throw IllegalParameterException(e.message, e)
        }
    }

    override fun box(option: ImageFormatOption): Map<String, List<String>> {
        return mapOf(PARAM to listOf(option.format.name))
    }
}
