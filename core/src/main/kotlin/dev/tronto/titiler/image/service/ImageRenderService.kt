package dev.tronto.titiler.image.service

import dev.tronto.titiler.core.domain.Ordered
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.getOrNull
import dev.tronto.titiler.core.outgoing.adaptor.gdal.logger
import dev.tronto.titiler.image.domain.Image
import dev.tronto.titiler.image.domain.ImageFormat
import dev.tronto.titiler.image.incoming.controller.option.ImageFormatOption
import dev.tronto.titiler.image.incoming.controller.option.RenderOption
import dev.tronto.titiler.image.incoming.controller.option.RescaleOption
import dev.tronto.titiler.image.incoming.usecase.ImageRenderUseCase
import dev.tronto.titiler.image.outgoing.port.ImageData
import dev.tronto.titiler.image.outgoing.port.ImageDataAutoRescale
import dev.tronto.titiler.image.outgoing.port.ImageDataRenderer
import java.util.*

class ImageRenderService(
    private val imageDataRenderers: List<ImageDataRenderer> =
        ServiceLoader.load(ImageDataRenderer::class.java, Thread.currentThread().contextClassLoader)
            .sortedBy { if (it is Ordered) it.order else 0 }.toList(),
    private val imageDataAutoRescales: List<ImageDataAutoRescale> =
        ServiceLoader.load(ImageDataAutoRescale::class.java, Thread.currentThread().contextClassLoader)
            .sortedBy { if (it is Ordered) it.order else 0 }.toList(),
) : ImageRenderUseCase {

    private fun renderImage(imageData: ImageData, format: ImageFormat): Image? {
        imageDataRenderers.forEach {
            if (it.supports(imageData, format)) {
                val imageBytes = it.render(imageData, format)
                return Image(imageBytes, format)
            }
        }
        return null
    }

    override suspend fun renderImage(imageData: ImageData, renderOptions: OptionProvider<RenderOption>): Image {
        val rescaleOption: RescaleOption? = renderOptions.getOrNull()
        val formatOption: ImageFormatOption? = renderOptions.getOrNull()

        val rescaledImageData = if (rescaleOption != null) {
            imageData.rescaleToUInt8(rescaleOption.rescale)
        } else {
            imageData
        }

        val format = formatOption?.format ?: if (rescaledImageData.masked) {
            ImageFormat.PNG
        } else {
            ImageFormat.JPEG
        }

        renderImage(rescaledImageData, format)?.let { return it }

        imageDataAutoRescales.forEach {
            if (it.supports(rescaledImageData, format)) {
                val autoRescaled = it.rescale(rescaledImageData, format)
                renderImage(autoRescaled, format)?.let {
                    logger.warn {
                        "Invalid type: ${rescaledImageData.dataType} for the $format driver. " +
                            "It will be auto rescaled."
                    }
                    return it
                }
            }
        }

        throw UnsupportedOperationException("Image cannot rendered.")
    }
}
