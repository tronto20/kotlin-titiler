package dev.tronto.titiler.image.outgoing.adaptor.imageio

import dev.tronto.titiler.core.domain.DataType
import dev.tronto.titiler.core.domain.Ordered
import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.image.domain.ImageFormat
import dev.tronto.titiler.image.outgoing.adaptor.multik.NDArrayImageData
import dev.tronto.titiler.image.outgoing.port.ImageRenderer
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.operations.stack
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import org.jetbrains.kotlinx.multik.ndarray.operations.toIntArray
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class NDArrayImageIOJpegRenderer : ImageRenderer, Ordered {
    companion object {
        private val SUPPORT_DATATYPE = listOf(DataType.UInt8)
        private val SUPPORT_BAND = intArrayOf(3)
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    override fun supports(imageData: ImageData, format: ImageFormat): Boolean {
        return imageData is NDArrayImageData<*> &&
            format == ImageFormat.JPEG &&
            imageData.dataType in SUPPORT_DATATYPE &&
            imageData.band in SUPPORT_BAND
    }

    override fun render(imageData: ImageData, format: ImageFormat): ByteArray {
        require(supports(imageData, format))
        val data = (imageData as NDArrayImageData<*>).data.asType<Int>()
        val mask = imageData.mask
        val shape = data.shape
        val width = shape[2]
        val height = shape[1]

        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val d3 = mk.stack(mask, mask, mask)
        val arr = data.times(d3.asType<Int>()).transpose(1, 2, 0).flatten().toIntArray()
        image.raster.setPixels(0, 0, width, height, arr)
        return ByteArrayOutputStream().use {
            ImageIO.write(image, "JPEG", it)
            it.toByteArray()
        }
    }
}
