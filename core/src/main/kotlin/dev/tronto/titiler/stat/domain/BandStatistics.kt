package dev.tronto.titiler.stat.domain

import kotlinx.serialization.Serializable

@Serializable
data class BandStatistics(
    val min: Double,
    val max: Double,
    val mean: Double,
    val count: Int,
    val sum: Double,
    val std: Double,
    val median: Double,
    val majority: Double,
    val minority: Double,
    val unique: Int,
    val validPercent: Double,
    val maskedPixels: Int,
    val validPixels: Int,
    val percentile: Map<Percentile, Double>,
)
