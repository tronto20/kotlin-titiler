package dev.tronto.titiler.image.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.image.domain.Image
import dev.tronto.titiler.image.incoming.controller.option.RenderOption
import dev.tronto.titiler.image.outgoing.port.ImageData

interface ImageRenderUseCase {
    suspend fun renderImage(imageData: ImageData, renderOptions: OptionProvider<RenderOption>): Image
}
