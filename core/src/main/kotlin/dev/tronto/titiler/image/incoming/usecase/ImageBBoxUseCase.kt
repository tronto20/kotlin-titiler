package dev.tronto.titiler.image.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.image.incoming.controller.option.FeatureOption
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.ImageSizeOption
import dev.tronto.titiler.image.incoming.controller.option.MaxSizeOption
import dev.tronto.titiler.image.incoming.controller.option.WindowOption
import dev.tronto.titiler.image.outgoing.port.ImageData

interface ImageBBoxUseCase : ImageReadUseCase {
    suspend fun bbox(openOptions: OptionProvider<OpenOption>, imageOptions: OptionProvider<ImageOption>): ImageData {
        imageOptions.get<WindowOption>()
        val imageSizeOption = imageOptions.getOrNull<ImageSizeOption>()
        val maxSizeOption = imageOptions.getOrNull<MaxSizeOption>()
        val overrideImageOptions = if (imageSizeOption == null && maxSizeOption == null) {
            imageOptions.remove<FeatureOption>() + MaxSizeOption(4096)
        } else {
            imageOptions.remove<FeatureOption>()
        }
        return read(
            openOptions,
            overrideImageOptions
        )
    }
}
