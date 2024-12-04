package dev.tronto.titiler.tile.service

import dev.tronto.titiler.core.exception.UnsupportedCrsStringException
import dev.tronto.titiler.core.incoming.controller.option.CRSOption
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.get
import dev.tronto.titiler.core.incoming.controller.option.getOrNull
import dev.tronto.titiler.core.incoming.controller.option.plus
import dev.tronto.titiler.core.incoming.usecase.InfoUseCase
import dev.tronto.titiler.core.outgoing.adaptor.gdal.GdalRasterFactory
import dev.tronto.titiler.core.outgoing.adaptor.gdal.SpatialReferenceCRSFactory
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.Raster
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import dev.tronto.titiler.core.service.CoreService
import dev.tronto.titiler.image.domain.Image
import dev.tronto.titiler.image.domain.Window
import dev.tronto.titiler.image.exception.ImageOutOfBoundsException
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.ImageSizeOption
import dev.tronto.titiler.image.incoming.controller.option.RenderOption
import dev.tronto.titiler.image.incoming.controller.option.WindowOption
import dev.tronto.titiler.image.incoming.usecase.ImageReadUseCase
import dev.tronto.titiler.image.incoming.usecase.ImageRenderUseCase
import dev.tronto.titiler.image.service.ImageRenderService
import dev.tronto.titiler.image.service.ImageService
import dev.tronto.titiler.tile.domain.TileInfo
import dev.tronto.titiler.tile.domain.TileMatrix
import dev.tronto.titiler.tile.domain.TileMatrixSet
import dev.tronto.titiler.tile.exception.TileCoordinateOutOfBoundsException
import dev.tronto.titiler.tile.exception.TileNotFoundException
import dev.tronto.titiler.tile.incoming.controller.option.TileCoordinateOption
import dev.tronto.titiler.tile.incoming.controller.option.TileMatrixSetOption
import dev.tronto.titiler.tile.incoming.controller.option.TileOption
import dev.tronto.titiler.tile.incoming.controller.option.TileScaleOption
import dev.tronto.titiler.tile.incoming.usecase.TileInfoUseCase
import dev.tronto.titiler.tile.incoming.usecase.TileUseCase
import dev.tronto.titiler.tile.outgoing.adaptor.resource.ResourceTileMatrixSetFactory
import dev.tronto.titiler.tile.outgoing.port.TileMatrixSetFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.locationtech.jts.geom.CoordinateXY
import org.locationtech.jts.geom.Envelope
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

class TileService(
    private val tileMatrixSetFactory: TileMatrixSetFactory = ResourceTileMatrixSetFactory(),
    private val crsFactory: CRSFactory = SpatialReferenceCRSFactory,
    private val rasterFactory: RasterFactory = GdalRasterFactory(crsFactory),
    private val imageReadUseCase: ImageReadUseCase = ImageService(crsFactory),
    private val infoUseCase: InfoUseCase = CoreService(rasterFactory),
    private val imageRenderUseCase: ImageRenderUseCase = ImageRenderService(),
) : TileUseCase, TileInfoUseCase {
    companion object {
        @JvmStatic
        private val logger = KotlinLogging.logger { }
    }

    private val crsTileMatrixSetCache = mutableMapOf<String, CRSTileMatrixSet>()

    private fun crsTileMatrixSet(tileMatrixSet: TileMatrixSet): CRSTileMatrixSet {
        return crsTileMatrixSetCache.getOrPut(tileMatrixSet.id) {
            val crs = tileMatrixSet.crs?.let { crsFactory.create(it) }
                ?: throw UnsupportedCrsStringException("null")
            CRSTileMatrixSet(tileMatrixSet, crs)
        }
    }

    private fun getMaximumOverviewLevel(width: Int, height: Int, minsize: Int = 256): Int {
        var overviewLevel = 0
        var overviewFactor = 1
        while (min(width / overviewFactor, height / overviewFactor) > minsize) {
            overviewLevel += 1
            overviewFactor *= 2
        }
        return overviewLevel
    }

    private fun tileBounds(
        tileMatrixSet: CRSTileMatrixSet,
        matrix: TileMatrix,
        tileCoord: TileCoordinateOption,
    ): Envelope {
        val resolution = tileMatrixSet.resolution(matrix)
        val isInvertAxis = tileMatrixSet.isInvertAxis
        val matrixOrigin = if (isInvertAxis) {
            CoordinateXY(
                matrix.pointOfOrigin.y,
                matrix.pointOfOrigin.x
            )
        } else {
            CoordinateXY(
                matrix.pointOfOrigin.x,
                matrix.pointOfOrigin.y
            )
        }

        val variableMatrixWidth = matrix.variableMatrixWidths?.find {
            tileCoord.y in it.minTileRow..it.maxTileRow
        }
        val cf = variableMatrixWidth?.coalesce ?: 1

        val upperLeft = CoordinateXY(
            matrixOrigin.x + floor(tileCoord.x.toDouble() / cf) * resolution * cf * matrix.tileWidth,
            matrixOrigin.y - tileCoord.y * resolution * matrix.tileHeight
        )
        val lowerRight = CoordinateXY(
            matrixOrigin.x + (floor(tileCoord.x.toDouble() / cf) + 1) * resolution * cf * matrix.tileWidth,
            matrixOrigin.y - (tileCoord.y + 1) * resolution * matrix.tileHeight
        )
        val tileBounds = Envelope(upperLeft, lowerRight)
        return tileBounds
    }

    private fun window(rasterCRSBounds: Envelope, raster: Raster): Window {
        val coords = listOf(
            CoordinateXY(rasterCRSBounds.minX, rasterCRSBounds.minY),
            CoordinateXY(rasterCRSBounds.minX, rasterCRSBounds.maxY),
            CoordinateXY(rasterCRSBounds.maxX, rasterCRSBounds.minY),
            CoordinateXY(rasterCRSBounds.maxX, rasterCRSBounds.maxY)
        ).map {
            raster.pixelCoordinateTransform.transformTo(it)
        }

        val rangeX = coords.minOf { it.x }.roundToInt()..coords.maxOf { it.x }.roundToInt()
        val rangeY = coords.minOf { it.y }.roundToInt()..coords.maxOf { it.y }.roundToInt()
        return Window(
            rangeX.first,
            rangeY.first,
            rangeX.last - rangeX.first,
            rangeY.last - rangeY.first
        )
    }

    override suspend fun tile(
        openOptions: OptionProvider<OpenOption>,
        imageOptions: OptionProvider<ImageOption>,
        tileOptions: OptionProvider<TileOption>,
        renderOptions: OptionProvider<RenderOption>,
    ): Image {
        val tileMatrixSet = tileMatrixSet(tileOptions)
        val tileCoord: TileCoordinateOption = tileOptions.get()
        val tileMatrix = tileMatrixSet[tileCoord.z]
        if (tileCoord.x !in 0..<tileMatrix.matrixWidth) {
            throw TileCoordinateOutOfBoundsException("x", tileCoord.x, 0..<tileMatrix.matrixWidth)
        }
        if (tileCoord.y !in 0..<tileMatrix.matrixHeight) {
            throw TileCoordinateOutOfBoundsException("y", tileCoord.y, 0..<tileMatrix.matrixHeight)
        }
        val crsTileMatrixSet = crsTileMatrixSet(tileMatrixSet)

        val tileBounds = tileBounds(crsTileMatrixSet, tileMatrix, tileCoord)
        val scaleOption: TileScaleOption? = tileOptions.getOrNull()
        val scale = scaleOption?.scale
        val tileWidth = if (scale == null) tileMatrix.tileWidth else tileMatrix.tileWidth * scale
        val tileHeight = if (scale == null) tileMatrix.tileHeight else tileMatrix.tileHeight * scale

        val crsOption = CRSOption(crsTileMatrixSet.crs.wkt)
        val tileOpenOptions = openOptions + crsOption

        val imageSizeOption = ImageSizeOption(
            tileWidth,
            tileHeight
        )
        val window = rasterFactory.withRaster(tileOpenOptions) {
            window(tileBounds, it)
        }
        val windowOption = WindowOption(window)

        val tileImageOptions = imageOptions + imageSizeOption + windowOption

        val imageData = try {
            imageReadUseCase.read(tileOpenOptions, tileImageOptions)
        } catch (e: ImageOutOfBoundsException) {
            throw TileNotFoundException(tileCoord, e)
        }
        return imageRenderUseCase.renderImage(imageData, renderOptions)
    }

    private suspend fun tileMatrixSet(tileOptions: OptionProvider<TileOption>): TileMatrixSet {
        val option: TileMatrixSetOption? = tileOptions.getOrNull()
        val tileMatrixSetId = option?.tileMatrixSetId
        val tileMatrixSet = tileMatrixSetId?.let { tileMatrixSetFactory.fromId(it) } ?: tileMatrixSetFactory.default()
        return tileMatrixSet
    }

    private fun getMinMaxZoom(tileMatrixSet: CRSTileMatrixSet, raster: Raster): Pair<Int, Int> {
        val bounds = raster.bounds()
        val resolution =
            max(abs((bounds.maxX - bounds.minX) / raster.width), abs((bounds.maxY - bounds.minY) / raster.height))
        val minZoom =
            kotlin.runCatching {
                val overviewLevel = getMaximumOverviewLevel(raster.width, raster.height)
                val ovrResolution = resolution * (2.0.pow(overviewLevel))
                tileMatrixSet.zoomForResolution(ovrResolution)
            }.recover {
                logger.warn(it) { "Cannot determine min zoom, will default to TMS min zoom." }
                tileMatrixSet.minzoom
            }.getOrThrow()
        val maxZoom =
            kotlin.runCatching {
                tileMatrixSet.zoomForResolution(resolution)
            }.recover {
                logger.warn(it) { "Cannot determine max zoom, will default to TMS max zoom." }
                tileMatrixSet.maxzoom
            }.getOrThrow()
        return minZoom to maxZoom
    }

    override suspend fun tileInfo(
        openOptions: OptionProvider<OpenOption>,
        tileOptions: OptionProvider<TileOption>,
    ): TileInfo {
        val tileMatrixSet = tileMatrixSet(tileOptions)
        val crsTileMatrixSet = crsTileMatrixSet(tileMatrixSet)
        val crsOption = CRSOption(crsTileMatrixSet.crs.wkt)
        val tileOpenOptions = openOptions + crsOption

        val (minZoom, maxZoom) = rasterFactory.withRaster(tileOpenOptions) {
            getMinMaxZoom(crsTileMatrixSet, it)
        }
        val info = infoUseCase.getInfo(tileOpenOptions)
        return TileInfo(
            minZoom,
            maxZoom,
            info
        )
    }
}
