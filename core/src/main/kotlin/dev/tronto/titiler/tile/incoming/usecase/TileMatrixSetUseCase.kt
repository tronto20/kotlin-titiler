package dev.tronto.titiler.tile.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.tile.domain.TileMatrixSet
import dev.tronto.titiler.tile.incoming.controller.option.TileMatrixSetOption

interface TileMatrixSetUseCase {
    suspend fun tileMatrixSets(): List<TileMatrixSet>
    suspend fun tileMatrixSet(options: OptionProvider<TileMatrixSetOption>): TileMatrixSet
}
