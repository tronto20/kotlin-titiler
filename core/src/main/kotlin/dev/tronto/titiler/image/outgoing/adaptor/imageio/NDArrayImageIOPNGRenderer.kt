package dev.tronto.titiler.image.outgoing.adaptor.imageio

import dev.tronto.titiler.core.domain.DataType
import dev.tronto.titiler.core.domain.Ordered
import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.image.domain.ImageFormat
import dev.tronto.titiler.image.outgoing.adaptor.multik.NDArrayImageData
import dev.tronto.titiler.image.outgoing.port.ImageRenderer
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D3
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.slice
import org.jetbrains.kotlinx.multik.ndarray.operations.or
import org.jetbrains.kotlinx.multik.ndarray.operations.stack
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import org.jetbrains.kotlinx.multik.ndarray.operations.toIntArray
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class NDArrayImageIOPNGRenderer : ImageRenderer, Ordered {
    companion object {
        private val SUPPORT_DATATYPE = listOf(DataType.UInt8)
        private val SUPPORT_BAND = intArrayOf(3, 4)
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    override fun supports(imageData: ImageData, format: ImageFormat): Boolean {
        return imageData is NDArrayImageData<*> &&
            format == ImageFormat.PNG &&
            imageData.data.shape.size == 3 &&
            imageData.dataType in SUPPORT_DATATYPE &&
            imageData.band in SUPPORT_BAND
    }

    override fun render(imageData: ImageData, format: ImageFormat): ByteArray {
        require(supports(imageData, format))
        val data = (imageData as NDArrayImageData<*>).data.asType<Int>()
        val mask = imageData.mask

        val shape = data.shape
        val band = shape[0]
        val width = shape[2]
        val height = shape[1]

        val image = when (band) {
            3 -> {
                val alpha = mask.asType<Int>() * 255
                val rgba = mk.stack(data[0], data[1], data[2], alpha)
                    .transpose(1, 2, 0)
                    .flatten()
                    .toIntArray()
                BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply {
                    raster.setPixels(0, 0, width, height, rgba)
                }
            }

            4 -> {
                val rgb = data.slice<Int, D3, D3>(0..2)
                    .transpose(1, 2, 0)
                    .flatten()
                    .toIntArray()
                val alphaBand = data[3]
                val alphaMask = (mask.asType<Int>() * 255)
                // alphaBand 에 값이 있다면 alphaMask 의 값이 0 일것.
                val alpha = alphaMask.or(alphaBand).flatten().toIntArray()
                BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB).apply {
                    raster.setPixels(0, 0, width, height, rgb)
                    alphaRaster.setPixels(0, 0, width, height, alpha)
                }
            }

            else -> throw UnsupportedOperationException("$band band not supported for png.")
        }
        return ByteArrayOutputStream().use {
            ImageIO.write(image, "PNG", it)
            it.toByteArray()
        }
    }
}
