package dev.tronto.titiler.core.outgoing.adaptor.gdal

import dev.tronto.titiler.core.outgoing.port.CRS
import org.gdal.osr.SpatialReference
import org.gdal.osr.osr

class SpatialReferenceCRS(
    val srs: SpatialReference,
) : CRS {
    override val name: String = srs.GetName()
    override val semiMajor: Double = srs.GetSemiMajor()
    override val semiMinor: Double = srs.GetSemiMinor()
    override val invertAxis: Boolean by lazy {
        srs.GetAxisOrientation("GEOGCS", 0) in listOf(
            osr.OAO_Down,
            osr.OAO_Up,
            osr.OAO_North,
            osr.OAO_South
        )
    }
    override val wkt: String = srs.ExportToWkt()
    override val proj4: String = srs.ExportToProj4()
    override val unit: String = srs.GetAttrValue("unit") ?: "metre"
}
