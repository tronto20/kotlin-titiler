package dev.tronto.titiler.image.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.outgoing.port.ImageData

interface ImageReadUseCase {
    suspend fun read(openOptions: OptionProvider<OpenOption>, imageOptions: OptionProvider<ImageOption>): ImageData
}
