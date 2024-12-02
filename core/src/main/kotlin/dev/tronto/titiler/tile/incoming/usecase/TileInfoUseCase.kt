package dev.tronto.titiler.tile.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.tile.domain.TileInfo
import dev.tronto.titiler.tile.incoming.controller.option.TileOption

interface TileInfoUseCase {
    suspend fun tileInfo(openOptions: OptionProvider<OpenOption>, tileOptions: OptionProvider<TileOption>): TileInfo
}
