package dev.tronto.titiler.image.spi

import dev.tronto.titiler.image.domain.ImageFormat

class DefaultImageFormatRegistrar : ImageFormatRegistrar {
    override fun imageFormats(): Iterable<ImageFormat> = listOf(ImageFormat.AUTO, ImageFormat.JPEG, ImageFormat.PNG)
}
