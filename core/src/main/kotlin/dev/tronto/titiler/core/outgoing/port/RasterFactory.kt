package dev.tronto.titiler.core.outgoing.port

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider

interface RasterFactory {
    suspend fun <T> withRaster(openOptions: OptionProvider<OpenOption>, block: (dataset: Raster) -> T): T
}
