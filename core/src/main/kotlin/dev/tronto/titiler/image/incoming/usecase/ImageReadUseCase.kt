package dev.tronto.titiler.image.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.image.incoming.controller.option.ImageOption

interface ImageReadUseCase {
    suspend fun read(openOptions: OptionProvider<OpenOption>, imageOptions: OptionProvider<ImageOption>): ImageData
}
