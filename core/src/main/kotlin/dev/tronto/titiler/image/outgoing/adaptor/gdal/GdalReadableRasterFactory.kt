package dev.tronto.titiler.image.outgoing.adaptor.gdal

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.outgoing.adaptor.gdal.GdalRasterFactory
import dev.tronto.titiler.core.outgoing.adaptor.gdal.SpatialReferenceCRSFactory
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import dev.tronto.titiler.image.outgoing.port.ReadableRaster
import dev.tronto.titiler.image.outgoing.port.ReadableRasterFactory

open class GdalReadableRasterFactory(
    private val crsFactory: CRSFactory = SpatialReferenceCRSFactory,
    private val gdalRasterFactory: GdalRasterFactory = GdalRasterFactory(crsFactory),
) : ReadableRasterFactory, RasterFactory by gdalRasterFactory {
    override suspend fun <T> withReadableRaster(
        openOptions: OptionProvider<OpenOption>,
        block: (dataset: ReadableRaster) -> T,
    ): T {
        return gdalRasterFactory.withGdalRaster(openOptions) { raster ->
            GdalReadableRaster(raster).use(block)
        }
    }
}
