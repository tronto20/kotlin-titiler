package io.github.tronto20.titiler.domain

import kotlinx.serialization.Serializable

@Serializable
data class BandMetadata(
    val name: String,
    val description: String? = null,
    val colorInterpolation: String? = null,
)
