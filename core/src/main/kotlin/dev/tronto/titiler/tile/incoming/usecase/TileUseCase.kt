package dev.tronto.titiler.tile.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.tile.incoming.controller.option.TileOption

interface TileUseCase {
    suspend fun tile(
        openOptions: OptionProvider<OpenOption>,
        imageOptions: OptionProvider<ImageOption>,
        tileOptions: OptionProvider<TileOption>,
    ): ImageData
}
