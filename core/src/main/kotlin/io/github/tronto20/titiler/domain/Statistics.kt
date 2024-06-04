package io.github.tronto20.titiler.domain

import kotlinx.serialization.Serializable

@Serializable
data class Statistics(
    val statistics: Map<BandIndex, BandStatistics>,
)
