package dev.tronto.titiler.stat.outgoing.port.spi

import dev.tronto.titiler.core.domain.Ordered
import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.stat.domain.BandStatistics
import dev.tronto.titiler.stat.domain.Percentile
import java.util.*

interface ImageDataStatistics {
    fun supports(imageData: ImageData): Boolean
    suspend fun statistics(imageData: ImageData, percentiles: List<Percentile>): List<BandStatistics>

    companion object {
        @JvmStatic
        val services by lazy {
            ServiceLoader.load(
                ImageDataStatistics::class.java,
                Thread.currentThread().contextClassLoader
            ).toList().sortedBy { if (it is Ordered) it.order else 0 }
        }
    }
}
