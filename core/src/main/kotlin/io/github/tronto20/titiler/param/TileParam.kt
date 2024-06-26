package io.github.tronto20.titiler.param

import io.github.tronto20.titiler.domain.ImageType

data class TileParam(
    val x: Int,
    val y: Int,
    val z: Int,
    val format: ImageType? = null,
    val scale: Int = 1,
    val rescale: List<OpenEndRange<Double>>? = null,
)
