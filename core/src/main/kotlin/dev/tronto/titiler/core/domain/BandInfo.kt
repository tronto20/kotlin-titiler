package dev.tronto.titiler.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class BandInfo(
    val description: String = "",
    val colorInterpolation: ColorInterpretation = ColorInterpretation.Undefined,
    val metadata: Map<String, String> = emptyMap(),
)
