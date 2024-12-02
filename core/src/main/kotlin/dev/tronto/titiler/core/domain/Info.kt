package dev.tronto.titiler.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Info(
    val bounds: DoubleArray,
    val dataType: DataType,
    val driver: String,
    val width: Int,
    val height: Int,
    val nodataType: String,
    val nodataValue: Double? = null,
    val bandCount: Int,
    val bandInfo: Map<BandIndex, BandInfo>,
)
