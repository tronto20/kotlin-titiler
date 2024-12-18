package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.image.domain.ImageFormat

@JvmInline
value class ImageFormatOption(val format: ImageFormat) : RenderOption
