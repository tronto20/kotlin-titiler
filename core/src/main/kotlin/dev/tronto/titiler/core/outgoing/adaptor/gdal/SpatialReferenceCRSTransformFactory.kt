package dev.tronto.titiler.core.outgoing.adaptor.gdal

import dev.tronto.titiler.core.outgoing.port.CRS
import dev.tronto.titiler.core.outgoing.port.CRSTransform
import dev.tronto.titiler.core.outgoing.port.CRSTransformFactory
import org.gdal.osr.CoordinateTransformation

object SpatialReferenceCRSTransformFactory : CRSTransformFactory {
    private val transformationCache = mutableMapOf<Pair<String, String>, CoordinateTransformation>()
    override fun create(source: CRS, destination: CRS): CRSTransform {
        if (source.proj4 == destination.proj4) {
            return CRSTransform.Empty
        }
        val sourceCRS = if (source is SpatialReferenceCRS) {
            source
        } else {
            SpatialReferenceCRSFactory.create(source.proj4) as SpatialReferenceCRS
        }
        val destinationCRS = if (destination is SpatialReferenceCRS) {
            destination
        } else {
            SpatialReferenceCRSFactory.create(destination.proj4) as SpatialReferenceCRS
        }
        val transform = transformationCache.getOrPut(sourceCRS.proj4 to destinationCRS.proj4) {
            CoordinateTransformation(sourceCRS.srs, destinationCRS.srs)
        }
        return SpatialReferenceCRSTransform(transform)
    }
}
