package io.github.tronto20.titiler.domain

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.locationtech.jts.geom.Envelope

@Serializable
data class Info(
    val bounds: @Contextual Envelope,
    val minZoom: Int,
    val maxZoom: Int,
    val bandMetadata: List<BandMetadata>,
    val dataType: String,
    val nodataType: String,
    val driver: String,
    val width: Int,
    val height: Int,
    val nodataValue: Int? = null,
)
