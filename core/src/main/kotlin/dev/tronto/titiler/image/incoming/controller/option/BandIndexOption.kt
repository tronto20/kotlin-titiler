package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.domain.BandIndex
import dev.tronto.titiler.core.incoming.controller.option.OpenOption

@JvmInline
value class BandIndexOption(val bandIndexes: List<BandIndex>) : OpenOption {
    init {
        require(bandIndexes.isNotEmpty()) {
            "BandIndexOption cannot be empty"
        }
    }
}
