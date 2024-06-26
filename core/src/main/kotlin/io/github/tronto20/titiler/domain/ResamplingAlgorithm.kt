package io.github.tronto20.titiler.domain

import io.github.tronto20.titiler.GdalInit
import kotlinx.serialization.Serializable
import org.gdal.gdalconst.gdalconst

@Serializable
enum class ResamplingAlgorithm(val gdalconst: Int) {
    NEAREST(gdalconst.GRA_NearestNeighbour),
    BILINEAR(gdalconst.GRA_Bilinear),
    CUBIC(gdalconst.GRA_Cubic),
    CUBIC_SPLINE(gdalconst.GRA_CubicSpline),
    LANCZOS(gdalconst.GRA_Lanczos),
    AVERAGE(gdalconst.GRA_Average),
    RMS(gdalconst.GRA_RMS),
    MODE(gdalconst.GRA_Mode),
    GAUSS(gdalconst.GRIORA_Gauss),
    ;

    companion object {
        init {
            GdalInit
        }
    }
}
