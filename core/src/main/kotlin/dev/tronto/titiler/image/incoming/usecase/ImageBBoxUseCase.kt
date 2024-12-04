package dev.tronto.titiler.image.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.filterNot
import dev.tronto.titiler.core.incoming.controller.option.get
import dev.tronto.titiler.core.incoming.controller.option.getOrNull
import dev.tronto.titiler.core.incoming.controller.option.plus
import dev.tronto.titiler.image.incoming.controller.option.FeatureOption
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.ImageSizeOption
import dev.tronto.titiler.image.incoming.controller.option.MaxSizeOption
import dev.tronto.titiler.image.incoming.controller.option.WindowOption
import dev.tronto.titiler.image.outgoing.port.ImageData

interface ImageBBoxUseCase : ImageReadUseCase {
    suspend fun bbox(openOptions: OptionProvider<OpenOption>, imageOptions: OptionProvider<ImageOption>): ImageData {
        val windowOption: WindowOption = imageOptions.get()
        val imageSizeOption: ImageSizeOption? = imageOptions.getOrNull()
        val maxSizeOption: MaxSizeOption? = imageOptions.getOrNull()
        val overrideImageOptions = if (imageSizeOption == null && maxSizeOption == null) {
            imageOptions.filterNot(ArgumentType<FeatureOption>()) + MaxSizeOption(4096)
        } else {
            imageOptions.filterNot(ArgumentType<FeatureOption>())
        }
        return read(
            openOptions,
            overrideImageOptions
        )
    }
}
