package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.domain.BandIndex

@JvmInline
value class BandIndexOption(
    val bandIndexes: List<BandIndex>,
) : ImageOption {
    init {
        require(bandIndexes.isNotEmpty()) {
            "BandIndexOption cannot be empty"
        }
    }
}
