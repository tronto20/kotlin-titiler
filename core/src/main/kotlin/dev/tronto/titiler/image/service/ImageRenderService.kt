package dev.tronto.titiler.image.service

import dev.tronto.titiler.core.domain.OptionContext
import dev.tronto.titiler.core.domain.Ordered
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.getOrNull
import dev.tronto.titiler.core.utils.logTrace
import dev.tronto.titiler.image.domain.Image
import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.image.domain.ImageFormat
import dev.tronto.titiler.image.incoming.controller.option.ImageFormatOption
import dev.tronto.titiler.image.incoming.controller.option.RenderOption
import dev.tronto.titiler.image.incoming.controller.option.RescaleOption
import dev.tronto.titiler.image.incoming.usecase.ImageRenderUseCase
import dev.tronto.titiler.image.outgoing.adaptor.imageio.SimpleImage
import dev.tronto.titiler.image.outgoing.port.ImageDataAutoRescale
import dev.tronto.titiler.image.outgoing.port.ImageRenderer
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.*

class ImageRenderService(
    private val imageRenderers: List<ImageRenderer> =
        ServiceLoader.load(ImageRenderer::class.java, Thread.currentThread().contextClassLoader)
            .sortedBy { if (it is Ordered) it.order else 0 }.toList(),
    private val imageDataAutoRescales: List<ImageDataAutoRescale> =
        ServiceLoader.load(ImageDataAutoRescale::class.java, Thread.currentThread().contextClassLoader)
            .sortedBy { if (it is Ordered) it.order else 0 }.toList(),
) : ImageRenderUseCase {
    companion object {
        @JvmStatic
        private val logger = KotlinLogging.logger { }
    }

    private suspend fun tryRender(imageData: ImageData, format: ImageFormat): Image? {
        imageRenderers.forEach {
            if (it.supports(imageData, format)) {
                val imageBytes = it.render(imageData, format)
                return SimpleImage(imageBytes, format)
            }
        }
        return null
    }

    private fun wrapOptions(imageData: ImageData, image: Image, vararg options: OptionProvider<*>): Image {
        if (image is OptionContext) {
            val optionProviders = if (imageData is OptionContext) {
                imageData.getAllOptionProviders()
            } else {
                emptyList()
            }
            image.put(*options, *optionProviders.toTypedArray())
        }
        return image
    }

    override suspend fun renderImage(imageData: ImageData, renderOptions: OptionProvider<RenderOption>): Image =
        logger.logTrace("image render") {
            val rescaleOption: RescaleOption? = renderOptions.getOrNull()
            val formatOption: ImageFormatOption? = renderOptions.getOrNull()

            val rescaledImageData = if (rescaleOption != null) {
                imageData.rescaleToUInt8(rescaleOption.rescale)
            } else {
                imageData
            }

            val format = if (formatOption == null || formatOption.format == ImageFormat.AUTO) {
                if (rescaledImageData.masked) {
                    ImageFormat.PNG
                } else {
                    ImageFormat.JPEG
                }
            } else {
                formatOption.format
            }

            tryRender(rescaledImageData, format)?.let { return@logTrace wrapOptions(imageData, it, renderOptions) }

            imageDataAutoRescales.forEach {
                if (it.supports(rescaledImageData, format)) {
                    val autoRescaled = it.rescale(rescaledImageData, format)
                    tryRender(autoRescaled, format)?.let {
                        logger.warn {
                            "Invalid type: ${rescaledImageData.dataType} for the $format driver. " +
                                "It will be auto rescaled."
                        }
                        return@logTrace wrapOptions(imageData, it, renderOptions)
                    }
                }
            }

            throw UnsupportedOperationException("Image cannot rendered.")
        }
}
