package dev.tronto.titiler.image.spi

import dev.tronto.titiler.image.domain.ImageFormat

fun interface ImageFormatRegistrar {
    fun imageFormats(): Iterable<ImageFormat>
}
