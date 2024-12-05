package dev.tronto.titiler.wmts.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.document.domain.Document
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.RenderOption
import dev.tronto.titiler.tile.incoming.controller.option.TileOption
import dev.tronto.titiler.wmts.incoming.controller.option.WMTSOption

interface WMTSUseCase {
    suspend fun wmts(
        openOptions: OptionProvider<OpenOption>,
        imageOptions: OptionProvider<ImageOption>,
        renderOptions: OptionProvider<RenderOption>,
        tileOptions: OptionProvider<TileOption>,
        wmtsOptions: OptionProvider<WMTSOption>,
    ): Document
}
