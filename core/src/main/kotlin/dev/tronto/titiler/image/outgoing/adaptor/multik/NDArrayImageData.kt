package dev.tronto.titiler.image.outgoing.adaptor.multik

import dev.tronto.titiler.core.domain.DataType
import dev.tronto.titiler.image.outgoing.port.ImageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.D3Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.view
import org.jetbrains.kotlinx.multik.ndarray.operations.all
import org.jetbrains.kotlinx.multik.ndarray.operations.mapMultiIndexed
import org.jetbrains.kotlinx.multik.ndarray.operations.stack
import org.locationtech.jts.algorithm.locate.IndexedPointInAreaLocator
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.Location

/**
 *  mask 는 0 이 유효하지 않은 값, 1이 유효한 값.
 */
internal sealed class NDArrayImageData<T>(
    internal val data: D3Array<T>,
    internal val mask: D2Array<Byte>,
) : ImageData where T : Comparable<T>, T : Number {
    final override val band
        get() = data.shape[0]

    final override val width
        get() = data.shape[2]

    final override val height
        get() = data.shape[1]

    final override val masked: Boolean
        get() = mask.all { true }

    init {
        val maskShape = mask.shape
        require(width == maskShape[1] && height == maskShape[0]) {
            "data and mask shape must be equal. (data: ${data.shape.toList()}, mask: ${mask.shape.toList()})"
        }
    }

    abstract fun copy(data: D3Array<T> = this.data, mask: D2Array<Byte> = this.mask): NDArrayImageData<T>

    override fun mask(geom: Geometry): ImageData {
        val index = IndexedPointInAreaLocator(geom)
        val mask = mask.mapMultiIndexed { (h, w), byte ->
            if (byte == 0.toByte()) {
                byte
            } else if (index.locate(Coordinate(w.toDouble(), h.toDouble())) == Location.EXTERIOR) {
                0.toByte()
            } else {
                1.toByte()
            }
        }
        return copy(mask = mask)
    }

    abstract fun Number.asType(): T

    override suspend fun <I, R> rescale(
        rangeFrom: List<ClosedRange<I>>,
        rangeTo: List<ClosedRange<R>>,
        dataType: DataType,
    ): ImageData where R : Number, I : Number, I : Comparable<I>, R : Comparable<R> {
        val rangeFrom = rangeFrom.map {
            NumberRange(it.start.asType()..it.endInclusive.asType())
        }
        return when (dataType) {
            DataType.Int8,
            DataType.UInt8,
            DataType.UInt16,
            DataType.Int16,
            DataType.Int32,
            DataType.CInt16,
            DataType.CInt32,
            -> {
                val rangeTo = rangeTo.map {
                    NumberRange(it.start.toInt()..it.endInclusive.toInt())
                }

                val rescaled = rescale(rangeFrom, rangeTo)
                IntImageData(rescaled, mask, dataType)
            }

            DataType.UInt32, DataType.Int64 -> {
                val rangeTo = rangeTo.map {
                    NumberRange(it.start.toLong()..it.endInclusive.toLong())
                }

                val rescaled = rescale(rangeFrom, rangeTo)
                LongImageData(rescaled, mask, dataType)
            }

            DataType.Float32, DataType.CFloat32 -> {
                val rangeTo = rangeTo.map {
                    NumberRange(it.start.toFloat()..it.endInclusive.toFloat())
                }

                val rescaled = rescale(rangeFrom, rangeTo)
                FloatImageData(rescaled, mask, dataType)
            }

            DataType.Float64, DataType.CFloat64 -> {
                val rangeTo = rangeTo.map {
                    NumberRange(it.start.toDouble()..it.endInclusive.toDouble())
                }

                val rescaled = rescale(rangeFrom, rangeTo)
                DoubleImageData(rescaled, mask, dataType)
            }

            DataType.UInt64 -> throw UnsupportedOperationException()
        }
    }

    private suspend inline fun <reified R> rescale(
        rangeFrom: List<NumberRange<T>>,
        rangeTo: List<NumberRange<R>>,
    ): D3Array<R> where R : Number, R : Comparable<R> {
        val rescaled = (0..<data.shape[0]).map { band ->
            val from = rangeFrom.getOrElse(band) { rangeFrom[0] }
            val to = rangeTo.getOrElse(band) { rangeTo[0] }
            CoroutineScope(Dispatchers.Default).async {
                // deepCopy 하지 않으면 데이터 오염 발생.
                val bandData = data.view(band).deepCopy()
                linearRescale<T, R>(bandData, from, to)
            }
        }
        return mk.stack(rescaled.awaitAll())
    }
}
