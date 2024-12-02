package dev.tronto.titiler.image.outgoing.port

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.outgoing.port.RasterFactory

interface ReadableRasterFactory : RasterFactory {
    suspend fun <T> withReadableRaster(
        openOptions: OptionProvider<OpenOption>,
        block: (dataset: ReadableRaster) -> T,
    ): T
}
