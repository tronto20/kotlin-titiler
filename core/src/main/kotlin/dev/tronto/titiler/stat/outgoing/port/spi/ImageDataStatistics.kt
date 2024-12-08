package dev.tronto.titiler.stat.outgoing.port.spi

import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.stat.domain.BandStatistics
import dev.tronto.titiler.stat.domain.Percentile

interface ImageDataStatistics {
    fun supports(imageData: ImageData): Boolean
    suspend fun statistics(imageData: ImageData, percentiles: List<Percentile>): List<BandStatistics>
}
