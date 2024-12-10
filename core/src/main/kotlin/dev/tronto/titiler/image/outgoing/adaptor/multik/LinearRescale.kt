package dev.tronto.titiler.image.outgoing.adaptor.multik

import dev.tronto.titiler.core.utils.logTrace
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import org.jetbrains.kotlinx.multik.ndarray.data.Dimension
import org.jetbrains.kotlinx.multik.ndarray.data.MemoryViewIntArray
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import org.jetbrains.kotlinx.multik.ndarray.operations.toDoubleArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toFloatArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toIntArray
import org.jetbrains.kotlinx.multik.ndarray.operations.toLongArray

val logger = KotlinLogging.logger {}
inline fun <T, D> linearRescale(
    data: MultiArray<T, D>,
    from: NumberRange<T>,
    to: NumberRange<Int>,
): NDArray<Int, D> where T : Number, T : Comparable<T>, D : Dimension = logger.logTrace("linear rescale") {
    val dimension = data.dim
    val shape = data.shape

    val ratio = to.gap / from.gap

    val toStart = to.start
    val toEnd = to.endInclusive
    val targetArray = when (data.dtype) {
        DataType.IntDataType -> {
            val array = (data as MultiArray<Int, D>).toIntArray()
            val targetArray = IntArray(array.size)
            val fromStart = from.start.toInt()
            val fromEnd = from.endInclusive.toInt()
            for (i in array.indices) {
                targetArray[i] = if (array[i] <= fromStart) {
                    toStart
                } else if (array[i] >= fromEnd) {
                    toEnd
                } else {
                    ((array[i] - fromStart) * ratio).toInt() + toStart
                }
            }
            targetArray
        }
        DataType.LongDataType -> {
            val array = (data as MultiArray<Long, D>).toLongArray()
            val targetArray = IntArray(array.size)
            val fromStart = from.start.toLong()
            val fromEnd = from.endInclusive.toLong()
            for (i in array.indices) {
                targetArray[i] = if (array[i] <= fromStart) {
                    toStart
                } else if (array[i] >= fromEnd) {
                    toEnd
                } else {
                    ((array[i] - fromStart) * ratio).toInt() + toStart
                }
            }
            targetArray
        }
        DataType.FloatDataType -> {
            val array = (data as MultiArray<Float, D>).toFloatArray()
            val targetArray = IntArray(array.size)
            val fromStart = from.start.toFloat()
            val fromEnd = from.endInclusive.toFloat()
            for (i in array.indices) {
                targetArray[i] = if (array[i] <= fromStart) {
                    toStart
                } else if (array[i] >= fromEnd) {
                    toEnd
                } else {
                    ((array[i] - fromStart) * ratio).toInt() + toStart
                }
            }
            targetArray
        }
        DataType.DoubleDataType -> {
            val array = (data as MultiArray<Double, D>).toDoubleArray()
            val targetArray = IntArray(array.size)
            val fromStart = from.start.toDouble()
            val fromEnd = from.endInclusive.toDouble()
            for (i in array.indices) {
                targetArray[i] = if (array[i] <= fromStart) {
                    toStart
                } else if (array[i] >= fromEnd) {
                    toEnd
                } else {
                    ((array[i] - fromStart) * ratio).toInt() + toStart
                }
            }
            targetArray
        }
        else -> throw UnsupportedOperationException()
    }

    NDArray(MemoryViewIntArray(targetArray), shape = shape, dim = dimension)
}
