package dev.tronto.titiler.tile.domain

import dev.tronto.titiler.core.domain.Info

data class TileInfo(
    val minZoom: Int,
    val maxZoom: Int,
    val info: Info,
)
