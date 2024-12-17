package dev.tronto.titiler.image.outgoing.adaptor

import dev.tronto.titiler.core.domain.OptionContext
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.image.domain.Image
import dev.tronto.titiler.image.domain.ImageFormat

class SimpleImage(
    override val data: ByteArray,
    override val format: ImageFormat,
    vararg val options: OptionProvider<*>,
) : Image, OptionContext by OptionContext.wrap(*options)
