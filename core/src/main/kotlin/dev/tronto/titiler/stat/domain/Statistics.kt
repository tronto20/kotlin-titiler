package dev.tronto.titiler.stat.domain

import dev.tronto.titiler.core.domain.BandIndex
import kotlinx.serialization.Serializable

@Serializable
data class Statistics(
    val statistics: Map<BandIndex, BandStatistics>,
)
