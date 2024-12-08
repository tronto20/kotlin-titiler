package dev.tronto.titiler.image.outgoing.adaptor.imageio

import dev.tronto.titiler.core.domain.DataType
import dev.tronto.titiler.core.domain.Ordered
import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.image.domain.ImageFormat
import dev.tronto.titiler.image.outgoing.adaptor.multik.IntImageData
import dev.tronto.titiler.image.outgoing.adaptor.multik.NDArrayImageData
import dev.tronto.titiler.image.outgoing.adaptor.multik.NumberRange
import dev.tronto.titiler.image.outgoing.adaptor.multik.linearRescale
import dev.tronto.titiler.image.outgoing.port.ImageDataAutoRescale
import dev.tronto.titiler.stat.domain.Percentile
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D3
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.view
import org.jetbrains.kotlinx.multik.ndarray.operations.sorted
import org.jetbrains.kotlinx.multik.ndarray.operations.stack

class NDArrayAutoRescale(
    private val percentile: ClosedRange<Percentile> = Percentile(2)..Percentile(98),
) : ImageDataAutoRescale, Ordered {
    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    override fun supports(imageData: ImageData, format: ImageFormat): Boolean {
        if (imageData !is NDArrayImageData<*>) return false
        if (format == ImageFormat.JPEG && imageData.band == 3) return true
        if (format == ImageFormat.PNG && imageData.band in listOf(3, 4)) return true

        return false
    }

    override suspend fun rescale(imageData: ImageData, format: ImageFormat): ImageData {
        val data = (imageData as NDArrayImageData<*>).data
        val size = imageData.width * imageData.height
        val start = size * percentile.start.value / 100
        val end = size * percentile.endInclusive.value / 100

        return when (imageData.dataType) {
            DataType.Int8,
            DataType.UInt8,
            DataType.Int16,
            DataType.UInt16,
            DataType.Int32,
            DataType.CInt16,
            DataType.CInt32,
            -> {
                rescaleWithIndex<Int>(imageData, data, start, end)
            }

            DataType.UInt32,
            DataType.Int64,
            -> {
                rescaleWithIndex<Long>(imageData, data, start, end)
            }

            DataType.Float32,
            DataType.CFloat32,
            -> {
                rescaleWithIndex<Float>(imageData, data, start, end)
            }

            DataType.Float64,
            DataType.CFloat64,
            -> {
                rescaleWithIndex<Double>(imageData, data, start, end)
            }

            DataType.UInt64 -> throw UnsupportedOperationException()
        }
    }

    private inline fun <reified T> rescaleWithIndex(
        imageData: NDArrayImageData<*>,
        data: NDArray<out Comparable<*>, D3>,
        start: Int,
        end: Int,
    ): IntImageData where T : Number, T : Comparable<T> {
        val rescaled = (0..<imageData.band).map {
            val typedData = data.asType<T>()
            val sorted = typedData.view(it).flatten().sorted()
            val from = sorted.get(start)..sorted.get(end)
            val a = typedData.view(it).deepCopy()
            linearRescale<T, Int>(
                a,
                NumberRange(from),
                NumberRange(0..255)
            )
        }
        return IntImageData(mk.stack(rescaled), imageData.mask, DataType.UInt8)
    }
}
