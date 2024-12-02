package dev.tronto.titiler.image.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.image.domain.Image
import dev.tronto.titiler.image.incoming.controller.option.FeatureOption
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.ImageSizeOption
import dev.tronto.titiler.image.incoming.controller.option.WindowOption

interface ImageBBoxUseCase : ImageReadUseCase {
    suspend fun bbox(openOptions: OptionProvider<OpenOption>, imageOptions: OptionProvider<ImageOption>): Image {
        imageOptions.get<WindowOption>()
        var imageOptions = imageOptions.remove<FeatureOption>()
        if (imageOptions.getOrNull<ImageSizeOption>() == null) {
            imageOptions = imageOptions + ImageSizeOption(4096, 4096)
        }
        return read(
            openOptions,
            imageOptions
        )
    }
}
