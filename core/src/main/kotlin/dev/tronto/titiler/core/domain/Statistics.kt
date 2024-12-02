package dev.tronto.titiler.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Statistics(
    val statistics: Map<BandIndex, BandStatistics>,
)
