package dev.tronto.titiler.stat.domain

import kotlinx.serialization.Serializable

@Serializable
data class Statistics(
    val statistics: List<BandStatistics>,
)
