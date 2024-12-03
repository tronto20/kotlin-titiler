package dev.tronto.titiler.core.outgoing.adaptor.gdal

import dev.tronto.titiler.core.outgoing.port.Raster
import org.gdal.gdal.Dataset

interface GdalBaseRaster : Raster {
    val dataset: Dataset
}
