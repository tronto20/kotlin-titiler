package io.github.tronto20.titiler.domain

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.locationtech.jts.geom.Envelope

@Serializable
data class Bounds(
    val bounds: @Contextual Envelope,
)
