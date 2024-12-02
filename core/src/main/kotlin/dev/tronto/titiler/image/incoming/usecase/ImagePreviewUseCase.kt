package dev.tronto.titiler.image.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.image.domain.Image
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.ImageSizeOption
import dev.tronto.titiler.image.incoming.controller.option.MaxSizeOption

interface ImagePreviewUseCase : ImageReadUseCase {
    suspend fun preview(openOptions: OptionProvider<OpenOption>, imageOptions: OptionProvider<ImageOption>): Image {
        val imageSizeOption = imageOptions.getOrNull<ImageSizeOption>()
        val maxSizeOption = imageOptions.getOrNull<MaxSizeOption>()
        val overrideImageOptions = if (imageSizeOption == null && maxSizeOption == null) {
            imageOptions + MaxSizeOption(4096)
        } else {
            imageOptions
        }
        return read(openOptions, overrideImageOptions)
    }
}
