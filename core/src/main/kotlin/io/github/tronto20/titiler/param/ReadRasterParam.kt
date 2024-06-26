package io.github.tronto20.titiler.param

import io.github.tronto20.titiler.domain.BandIndex
import io.github.tronto20.titiler.domain.ResamplingAlgorithm
import org.locationtech.jts.geom.Geometry

data class ReadRasterParam(
    val bandIndex: List<BandIndex>? = null,
    val noData: Number? = null,
    val geometry: Geometry? = null,
    val resampling: ResamplingAlgorithm = ResamplingAlgorithm.NEAREST,
    val reProjection: ResamplingAlgorithm = ResamplingAlgorithm.NEAREST,
)
