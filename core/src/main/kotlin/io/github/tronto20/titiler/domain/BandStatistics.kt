package io.github.tronto20.titiler.domain

import kotlinx.serialization.Serializable

@Serializable
data class BandStatistics(
    val min: Double,
    val max: Double,
    val mean: Double,
    val count: Double,
    val sum: Double,
    val std: Double,
    val median: Double,
    val majority: Double,
    val minority: Double,
    val unique: Double,
    val histogram: List<List<Double>>,
    val validPercent: Double,
    val maskedPixels: Double,
    val validPixels: Double,
    val percentile: Map<Percentile, Double>,
)
