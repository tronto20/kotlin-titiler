package dev.tronto.titiler.image.service

import dev.tronto.titiler.core.domain.OptionContext
import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.get
import dev.tronto.titiler.core.incoming.controller.option.getOrNull
import dev.tronto.titiler.core.outgoing.adaptor.gdal.SpatialReferenceCRSFactory
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.image.domain.Window
import dev.tronto.titiler.image.exception.ImageOutOfBoundsException
import dev.tronto.titiler.image.incoming.controller.option.BandIndexOption
import dev.tronto.titiler.image.incoming.controller.option.FeatureOption
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.ImageSizeOption
import dev.tronto.titiler.image.incoming.controller.option.MaxSizeOption
import dev.tronto.titiler.image.incoming.controller.option.WindowOption
import dev.tronto.titiler.image.incoming.usecase.ImageBBoxUseCase
import dev.tronto.titiler.image.incoming.usecase.ImagePreviewUseCase
import dev.tronto.titiler.image.incoming.usecase.ImageReadUseCase
import dev.tronto.titiler.image.outgoing.adaptor.gdal.GdalReadableRasterFactory
import dev.tronto.titiler.image.outgoing.port.ReadableRasterFactory
import org.locationtech.jts.geom.CoordinateXY
import org.locationtech.jts.geom.util.AffineTransformationFactory
import kotlin.math.roundToInt

class ImageService(
    private val crsFactory: CRSFactory = SpatialReferenceCRSFactory,
    private val readableRasterFactory: ReadableRasterFactory = GdalReadableRasterFactory(crsFactory),
) : ImageReadUseCase, ImageBBoxUseCase, ImagePreviewUseCase {
    override suspend fun read(
        openOptions: OptionProvider<OpenOption>,
        imageOptions: OptionProvider<ImageOption>,
    ): ImageData {
        val bandIndexOption: BandIndexOption? = imageOptions.getOrNull()

        val featureOption: FeatureOption? = imageOptions.getOrNull()
        val maxSizeOption: MaxSizeOption? = imageOptions.getOrNull()
        val imageSizeOption: ImageSizeOption? = imageOptions.getOrNull()

        val maskedImageData = readableRasterFactory.withReadableRaster(openOptions) { raster ->

            val pixelFeature = featureOption?.let {
                /**
                 *  1. polygon crs -> image crs
                 *  2. image crs -> pixel crs
                 */
                val polygonCRS = crsFactory.create(featureOption.crsString)
                val rasterCRSTransform = crsFactory.transformTo(polygonCRS, raster.crs)
                val polygon = rasterCRSTransform.transformTo(featureOption.polygon)
                raster.pixelCoordinateTransform.transformTo(polygon)
            }

            val rasterWindow = Window(0, 0, raster.width, raster.height)

            /**
             *  window 우선순위
             *  1. feature
             *  2. window
             *  3. whole raster
             */
            val window = if (pixelFeature != null) {
                val pixelEnvelope = pixelFeature.envelopeInternal
                Window.fromEnvelope(pixelEnvelope)
            } else {
                val windowOption: WindowOption? = imageOptions.getOrNull()
                windowOption?.window ?: Window(0, 0, raster.width, raster.height)
            }

            // window check
            if (!rasterWindow.toEnvelope().intersects(window.toEnvelope())) {
                throw ImageOutOfBoundsException(window, rasterWindow)
            }

            /**
             *  width, height 우선순위
             *  1. imageSize
             *  2. maxSize
             */

                val (width, height) = if (imageSizeOption != null) {
                imageSizeOption.width to imageSizeOption.height
            } else if (maxSizeOption != null) {
                val maxSize = maxSizeOption.maxSize
                val widthRatio = maxSize.toDouble() / window.width
                val heightRatio = maxSize.toDouble() / window.height

                val ratio = if (widthRatio < heightRatio) {
                    widthRatio
                } else {
                    heightRatio
                }
                (window.width * ratio).roundToInt() to (window.height * ratio).roundToInt()
            } else {
                val imageSizeOption: ImageSizeOption = imageOptions.get()
                imageSizeOption.width to imageSizeOption.height
            }

            if (width < 10 || height < 10) {
                throw IllegalParameterException("width or height must be greater than 10.")
            }

            val imageData = raster.read(window, width, height, bandIndexOption?.bandIndexes)
            val maskedImageData = if (pixelFeature != null) {
                val from = pixelFeature.envelopeInternal
                val transform = AffineTransformationFactory.createFromBaseLines(
                    CoordinateXY(from.minX, from.minY),
                    CoordinateXY(from.maxX, from.maxY),
                    CoordinateXY(0.0, 0.0),
                    CoordinateXY(window.width.toDouble(), window.height.toDouble())
                )
                imageData.mask(transform.transform(pixelFeature))
            } else {
                imageData
            }
            maskedImageData
        }
        if (maskedImageData is OptionContext) {
            maskedImageData.put(openOptions, imageOptions)
        }
        return maskedImageData
    }
}
