package dev.tronto.titiler.tile.service

import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.tile.domain.TileMatrixSet
import dev.tronto.titiler.tile.incoming.controller.option.TileMatrixSetOption
import dev.tronto.titiler.tile.incoming.usecase.TileMatrixSetUseCase
import dev.tronto.titiler.tile.outgoing.port.TileMatrixSetFactory

class TileMatrixSetService(private val tileMatrixSetFactory: TileMatrixSetFactory) : TileMatrixSetUseCase {
    override suspend fun tileMatrixSets(): List<TileMatrixSet> = tileMatrixSetFactory.list().toList()

    override suspend fun tileMatrixSet(options: OptionProvider<TileMatrixSetOption>): TileMatrixSet =
        tileMatrixSetFactory.fromId(options.get(ArgumentType<TileMatrixSetOption>()).tileMatrixSetId)
}
