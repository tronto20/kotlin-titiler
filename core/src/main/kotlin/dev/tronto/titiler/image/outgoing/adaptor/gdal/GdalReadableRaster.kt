package dev.tronto.titiler.image.outgoing.adaptor.gdal

import dev.tronto.titiler.core.domain.BandIndex
import dev.tronto.titiler.core.domain.DataType
import dev.tronto.titiler.core.outgoing.adaptor.gdal.GdalBaseRaster
import dev.tronto.titiler.core.outgoing.adaptor.gdal.gdalConst
import dev.tronto.titiler.core.outgoing.adaptor.gdal.handleError
import dev.tronto.titiler.image.domain.Window
import dev.tronto.titiler.image.outgoing.adaptor.multik.DoubleImageData
import dev.tronto.titiler.image.outgoing.adaptor.multik.FloatImageData
import dev.tronto.titiler.image.outgoing.adaptor.multik.IntImageData
import dev.tronto.titiler.image.outgoing.adaptor.multik.LongImageData
import dev.tronto.titiler.image.outgoing.port.ImageData
import dev.tronto.titiler.image.outgoing.port.ReadableRaster
import org.jetbrains.kotlinx.multik.api.d2arrayIndices
import org.jetbrains.kotlinx.multik.api.d3array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.ones
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.D3Array
import org.jetbrains.kotlinx.multik.ndarray.data.view
import org.jetbrains.kotlinx.multik.ndarray.operations.all
import kotlin.math.roundToInt

open class GdalReadableRaster(
    private val gdalRaster: GdalBaseRaster,
) : ReadableRaster, GdalBaseRaster by gdalRaster {

    private fun <T> mask(data: D3Array<T>, noData: T?): D2Array<Byte> {
        val shape = data.shape
        return if (noData == null) {
            mk.ones(shape[1], shape[2])
        } else {
            mk.d2arrayIndices(shape[1], shape[2]) { h, w ->
                if (data.view(h, w, 1, 2).all { it == noData }) {
                    0.toByte()
                } else {
                    1.toByte()
                }
            }
        }
    }

    private inline fun <reified T> pad(
        data: D3Array<T>,
        mask: D2Array<Byte>,
        noDataValue: T,
        left: Int,
        right: Int,
        upper: Int,
        lower: Int,
    ): Pair<D3Array<T>, D2Array<Byte>> where T : Number, T : Comparable<T> {
        var data = data
        var mask = mask

        if (left > 0) {
            val leftPad = if (noDataValue.toInt() == 0) {
                mk.zeros<T>(data.shape[0], data.shape[1], left)
            } else {
                mk.d3array(data.shape[0], data.shape[1], left) { noDataValue }
            }
            data = leftPad.cat(data, 2)
            mask = mk.zeros<Byte>(data.shape[1], left).cat(mask, 1)
        }

        if (right > 0) {
            val rightPad = if (noDataValue.toInt() == 0) {
                mk.zeros<T>(data.shape[0], data.shape[1], right)
            } else {
                mk.d3array(data.shape[0], data.shape[1], right) { noDataValue }
            }
            data = data.cat(rightPad, 2)
            mask = mask.cat(mk.zeros<Byte>(data.shape[1], right), 1)
        }

        if (upper > 0) {
            val upperPad = if (noDataValue.toInt() == 0) {
                mk.zeros<T>(data.shape[0], upper, data.shape[2])
            } else {
                mk.d3array(data.shape[0], upper, data.shape[2]) { noDataValue }
            }

            data = upperPad.cat(data, 1)
            mask = mk.zeros<Byte>(upper, data.shape[2]).cat(mask, 0)
        }

        if (lower > 0) {
            val lowerPad = if (noDataValue.toInt() == 0) {
                mk.zeros<T>(data.shape[0], lower, data.shape[2])
            } else {
                mk.d3array(data.shape[0], lower, data.shape[2]) { noDataValue }
            }

            data = data.cat(lowerPad, 1)
            mask = mask.cat(mk.zeros<Byte>(lower, data.shape[2]), 0)
        }

        return data to mask
    }

    override fun read(
        window: Window,
        width: Int,
        height: Int,
        bandIndexes: List<BandIndex>?,
        nodata: Number?,
    ): ImageData {
        val leftOver = if (window.xOffset < 0) -window.xOffset else 0
        val rightOver = if (window.xOffset + window.width > this.width) {
            window.xOffset + window.width - this.width
        } else {
            0
        }

        val upperOver = if (window.yOffset < 0) -window.yOffset else 0
        val lowerOver = if (window.yOffset + window.height > this.height) {
            window.yOffset + window.height - this.width
        } else {
            0
        }

        val widthRatio = width.toDouble() / window.width
        val heightRatio = height.toDouble() / window.height

        val leftPad = if (leftOver > 0) (leftOver * widthRatio).roundToInt() else 0
        val rightPad = if (rightOver > 0) (rightOver * widthRatio).roundToInt() else 0
        val upperPad = if (upperOver > 0) (upperOver * heightRatio).roundToInt() else 0
        val lowerPad = if (lowerOver > 0) (lowerOver * heightRatio).roundToInt() else 0

        val newWindow = Window(
            window.xOffset + leftOver,
            window.yOffset + upperOver,
            window.width - leftOver - rightOver,
            window.height - upperOver - lowerOver
        )
        val newWidth = width - leftPad - rightPad
        val newHeight = height - upperPad - lowerPad
        val bandList = bandIndexes?.map { it.value }?.toIntArray() ?: IntArray(bandCount) { it + 1 }

        return when (dataType) {
            DataType.Int8,
            DataType.UInt8,
            DataType.UInt16,
            DataType.Int16,
            DataType.Int32,
            DataType.CInt16,
            DataType.CInt32,
            -> {
                val data = readIntData(bandList, newWidth, newHeight, newWindow)
                val noDataValue = (nodata ?: this.noDataValue)?.toInt()
                val mask = mask(data, noDataValue)
                val (resultData, resultMask) = pad(
                    data,
                    mask,
                    noDataValue ?: 0,
                    leftPad,
                    rightPad,
                    upperPad,
                    lowerPad
                )
                IntImageData(resultData, resultMask, dataType)
            }

            DataType.UInt32, DataType.Int64 -> {
                val data = readLongData(bandList, newWidth, newHeight, newWindow)
                val noDataValue = (nodata ?: this.noDataValue)?.toLong()
                val mask = mask(data, noDataValue)
                val (resultData, resultMask) = pad(
                    data,
                    mask,
                    noDataValue ?: 0,
                    leftPad,
                    rightPad,
                    upperPad,
                    lowerPad
                )
                LongImageData(resultData, resultMask, dataType)
            }

            DataType.Float32, DataType.CFloat32 -> {
                val data = readFloatData(bandList, newWidth, newHeight, newWindow)
                val noDataValue = (nodata ?: this.noDataValue)?.toFloat()
                val mask = mask(data, noDataValue)
                val (resultData, resultMask) = pad(
                    data,
                    mask,
                    noDataValue ?: 0.0f,
                    leftPad,
                    rightPad,
                    upperPad,
                    lowerPad
                )
                FloatImageData(resultData, resultMask, dataType)
            }

            DataType.Float64, DataType.CFloat64 -> {
                val data = readDoubleData(bandList, newWidth, newHeight, newWindow)
                val noDataValue = (nodata ?: this.noDataValue)?.toDouble()
                val mask = mask(data, noDataValue)
                val (resultData, resultMask) = pad(
                    data,
                    mask,
                    noDataValue ?: 0.0,
                    leftPad,
                    rightPad,
                    upperPad,
                    lowerPad
                )
                DoubleImageData(resultData, resultMask, dataType)
            }

            else -> throw UnsupportedOperationException("$dataType is not supported yet.")
        }
    }

    private fun readDoubleData(bandList: IntArray, width: Int, height: Int, window: Window): D3Array<Double> {
        val arr = DoubleArray(bandList.size * width * height)
        dataset.handleError {
            ReadRaster(
                window.xOffset,
                window.yOffset,
                window.width,
                window.height,
                width,
                height,
                DataType.Float64.gdalConst,
                arr,
                bandList
            )
        }
        return mk.ndarray(arr, bandList.size, height, width)
    }

    private fun readFloatData(bandList: IntArray, width: Int, height: Int, window: Window): D3Array<Float> {
        val arr = FloatArray(bandList.size * width * height)
        dataset.handleError {
            ReadRaster(
                window.xOffset,
                window.yOffset,
                window.width,
                window.height,
                width,
                height,
                DataType.Float32.gdalConst,
                arr,
                bandList
            )
        }
        return mk.ndarray(arr, bandList.size, height, width)
    }

    private fun readLongData(bandList: IntArray, width: Int, height: Int, window: Window): D3Array<Long> {
        val arr = LongArray(bandList.size * width * height)
        dataset.handleError {
            ReadRaster(
                window.xOffset,
                window.yOffset,
                window.width,
                window.height,
                width,
                height,
                DataType.Int64.gdalConst,
                arr,
                bandList
            )
        }
        return mk.ndarray(arr, bandList.size, height, width)
    }

    private fun readIntData(bandList: IntArray, width: Int, height: Int, window: Window): D3Array<Int> {
        val arr = IntArray(bandList.size * width * height)
        dataset.handleError {
            ReadRaster(
                window.xOffset,
                window.yOffset,
                window.width,
                window.height,
                width,
                height,
                DataType.Int32.gdalConst,
                arr,
                bandList
            )
        }
        return mk.ndarray(arr, bandList.size, height, width)
    }

    override fun close() {
        dataset.delete()
    }
}
