package dev.tronto.titiler.image.service

import dev.tronto.titiler.core.domain.Ordered
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.outgoing.adaptor.gdal.SpatialReferenceCRSFactory
import dev.tronto.titiler.core.outgoing.adaptor.gdal.SpatialReferenceCRSTransformFactory
import dev.tronto.titiler.core.outgoing.adaptor.gdal.logger
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.CRSTransformFactory
import dev.tronto.titiler.image.domain.Image
import dev.tronto.titiler.image.domain.ImageFormat
import dev.tronto.titiler.image.domain.Window
import dev.tronto.titiler.image.exception.ImageOutOfBoundsException
import dev.tronto.titiler.image.incoming.controller.option.BandIndexOption
import dev.tronto.titiler.image.incoming.controller.option.FeatureOption
import dev.tronto.titiler.image.incoming.controller.option.ImageFormatOption
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.ImageSizeOption
import dev.tronto.titiler.image.incoming.controller.option.MaxSizeOption
import dev.tronto.titiler.image.incoming.controller.option.NoDataOption
import dev.tronto.titiler.image.incoming.controller.option.RescaleOption
import dev.tronto.titiler.image.incoming.controller.option.WindowOption
import dev.tronto.titiler.image.incoming.usecase.ImageBBoxUseCase
import dev.tronto.titiler.image.incoming.usecase.ImagePreviewUseCase
import dev.tronto.titiler.image.incoming.usecase.ImageReadUseCase
import dev.tronto.titiler.image.outgoing.adaptor.gdal.GdalReadableRasterFactory
import dev.tronto.titiler.image.outgoing.port.ImageData
import dev.tronto.titiler.image.outgoing.port.ImageDataAutoRescale
import dev.tronto.titiler.image.outgoing.port.ImageDataRenderer
import dev.tronto.titiler.image.outgoing.port.ReadableRasterFactory
import org.locationtech.jts.geom.CoordinateXY
import org.locationtech.jts.geom.util.AffineTransformationFactory
import java.util.*
import kotlin.math.roundToInt

class ImageService(
    private val crsFactory: CRSFactory = SpatialReferenceCRSFactory,
    private val crsTransformFactory: CRSTransformFactory = SpatialReferenceCRSTransformFactory,
    private val readableRasterFactory: ReadableRasterFactory = GdalReadableRasterFactory(crsFactory),
    private val imageDataRenderers: List<ImageDataRenderer> =
        ServiceLoader.load(ImageDataRenderer::class.java, Thread.currentThread().contextClassLoader)
            .sortedBy { if (it is Ordered) it.order else 0 }.toList(),
    private val imageDataAutoRescales: List<ImageDataAutoRescale> =
        ServiceLoader.load(ImageDataAutoRescale::class.java, Thread.currentThread().contextClassLoader)
            .sortedBy { if (it is Ordered) it.order else 0 }.toList(),
) : ImageReadUseCase, ImageBBoxUseCase, ImagePreviewUseCase {
    override suspend fun read(
        openOptions: OptionProvider<OpenOption>,
        imageOptions: OptionProvider<ImageOption>,
    ): Image {
        val bandIndexOption = imageOptions.getOrNull<BandIndexOption>()
        val nodataOption = imageOptions.getOrNull<NoDataOption>()
        val rescaleOption = imageOptions.getOrNull<RescaleOption>()
        val formatOption = imageOptions.getOrNull<ImageFormatOption>()

        val featureOption = imageOptions.getOrNull<FeatureOption>()
        val maxSizeOption = imageOptions.getOrNull<MaxSizeOption>()
        val imageSizeOption = imageOptions.getOrNull<ImageSizeOption>()

        val maskedImageData = readableRasterFactory.withReadableRaster(openOptions) { raster ->

            val pixelFeature = featureOption?.let {
                /**
                 *  1. polygon crs -> image crs
                 *  2. image crs -> pixel crs
                 */
                val polygonCRS = crsFactory.create(featureOption.crsString)
                val rasterCRSTransform = crsTransformFactory.create(polygonCRS, raster.crs)
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
                imageOptions.getOrNull<WindowOption>()?.window
                    ?: Window(0, 0, raster.width, raster.height)
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
                val imageSizeOption = imageOptions.get<ImageSizeOption>()
                imageSizeOption.width to imageSizeOption.height
            }

            val imageData = raster.read(window, width, height, bandIndexOption?.bandIndexes, nodataOption?.noData)
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

        val rescaledImageData = if (rescaleOption != null) {
            maskedImageData.rescaleToUInt8(rescaleOption.rescale)
        } else {
            maskedImageData
        }

        val format = formatOption?.format ?: if (rescaledImageData.masked) {
            ImageFormat.PNG
        } else {
            ImageFormat.JPEG
        }

        renderImage(rescaledImageData, format)?.let { return it }

        imageDataAutoRescales.forEach {
            if (it.supports(rescaledImageData, format)) {
                val autoRescaled = it.rescale(rescaledImageData, format)
                renderImage(autoRescaled, format)?.let {
                    logger.warn {
                        "Invalid type: ${rescaledImageData.dataType} for the $format driver. " +
                            "It will be auto rescaled."
                    }
                    return it
                }
            }
        }

        throw UnsupportedOperationException("Image cannot rendered.")
    }

    private fun renderImage(imageData: ImageData, format: ImageFormat): Image? {
        imageDataRenderers.forEach {
            if (it.supports(imageData, format)) {
                val imageBytes = it.render(imageData, format)
                return Image(imageBytes, format)
            }
        }
        return null
    }
}
