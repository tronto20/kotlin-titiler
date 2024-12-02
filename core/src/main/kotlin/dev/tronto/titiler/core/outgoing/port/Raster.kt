package dev.tronto.titiler.core.outgoing.port

import dev.tronto.titiler.core.domain.BandIndex
import dev.tronto.titiler.core.domain.BandInfo
import dev.tronto.titiler.core.domain.DataType
import org.locationtech.jts.geom.Envelope

interface Raster : AutoCloseable {
    val width: Int
    val height: Int
    val bandCount: Int
    val driver: String
    val dataType: DataType
    val noDataType: String
    val noDataValue: Double?
    val crs: CRS
    val pixelCoordinateTransform: CoordinateTransform

    fun bounds(): Envelope
    fun bandInfo(bandIndex: BandIndex): BandInfo
}
