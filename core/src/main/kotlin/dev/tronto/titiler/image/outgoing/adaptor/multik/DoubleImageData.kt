package dev.tronto.titiler.image.outgoing.adaptor.multik

import dev.tronto.titiler.core.domain.DataType
import dev.tronto.titiler.image.outgoing.port.ImageData
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.D3Array

internal class DoubleImageData(
    data: D3Array<Double>,
    mask: D2Array<Byte>,
    override val dataType: DataType,
) : ImageData, NDArrayImageData<Double>(data, mask) {
    companion object {
        @JvmStatic
        private val logger = KotlinLogging.logger { }
        private val availableTypes = listOf(
            DataType.Float64,
            DataType.CFloat64
        )
    }

    init {
        require(dataType in availableTypes) {
            "dataType must be in $availableTypes"
        }
    }

    override fun Number.asType(): Double {
        return toDouble()
    }

    override fun copy(data: D3Array<Double>, mask: D2Array<Byte>): NDArrayImageData<Double> {
        return DoubleImageData(data, mask, dataType)
    }
}
