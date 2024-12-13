package dev.tronto.titiler.image.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.getOrNull
import dev.tronto.titiler.core.incoming.controller.option.plus
import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.ImageSizeOption
import dev.tronto.titiler.image.incoming.controller.option.MaxSizeOption

interface ImagePreviewUseCase : ImageReadUseCase {
    suspend fun preview(openOptions: OptionProvider<OpenOption>, imageOptions: OptionProvider<ImageOption>): ImageData {
        val imageSizeOption: ImageSizeOption? = imageOptions.getOrNull()
        val maxSizeOption: MaxSizeOption? = imageOptions.getOrNull()
        val overrideImageOptions = if (imageSizeOption == null && maxSizeOption == null) {
            imageOptions + MaxSizeOption(1024)
        } else {
            imageOptions
        }
        return read(openOptions, overrideImageOptions)
    }
}
