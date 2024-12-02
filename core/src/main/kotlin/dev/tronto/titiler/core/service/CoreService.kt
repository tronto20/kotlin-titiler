package dev.tronto.titiler.core.service

import dev.tronto.titiler.core.domain.BandIndex
import dev.tronto.titiler.core.domain.Bounds
import dev.tronto.titiler.core.domain.Info
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.usecase.BoundsUseCase
import dev.tronto.titiler.core.incoming.usecase.InfoUseCase
import dev.tronto.titiler.core.outgoing.adaptor.gdal.GdalRasterFactory
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import org.locationtech.jts.geom.Envelope

class CoreService(
    private val rasterFactory: RasterFactory = GdalRasterFactory(),
) : BoundsUseCase, InfoUseCase {
    private fun Envelope.toArray() = doubleArrayOf(minX, minY, maxX, maxY)

    override suspend fun getBounds(openOptions: OptionProvider<OpenOption>): Bounds {
        val envelope = rasterFactory.withRaster(openOptions) {
            it.bounds()
        }
        return Bounds(
            bounds = envelope.toArray()
        )
    }

    override suspend fun getInfo(openOptions: OptionProvider<OpenOption>): Info {
        return rasterFactory.withRaster(openOptions) { raster ->
            Info(
                raster.bounds().toArray(),
                raster.dataType,
                raster.driver,
                raster.width,
                raster.height,
                raster.noDataType,
                raster.noDataValue,
                raster.bandCount,
                (1..raster.bandCount).map { BandIndex(it) }.associateWith { raster.bandInfo(it) }
            )
        }
    }
}
