package dev.tronto.titiler.core.outgoing.adaptor.gdal

import dev.tronto.titiler.core.exception.UnsupportedCrsStringException
import dev.tronto.titiler.core.outgoing.port.CRS
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import org.gdal.osr.SpatialReference

object SpatialReferenceCRSFactory : CRSFactory {
    private val spatialRefCache = mutableMapOf<String, SpatialReference>()

    override fun create(crsString: String): CRS {
        val spatialReference = spatialRefCache.getOrPut(crsString) {
            kotlin.runCatching {
                SpatialReference().apply { SetFromUserInput(crsString) }
            }.getOrElse {
                throw UnsupportedCrsStringException(crsString)
            }
        }
        return SpatialReferenceCRS(spatialReference)
    }
}
