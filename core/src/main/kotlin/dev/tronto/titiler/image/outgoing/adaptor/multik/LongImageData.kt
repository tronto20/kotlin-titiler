package dev.tronto.titiler.image.outgoing.adaptor.multik

import dev.tronto.titiler.core.domain.DataType
import dev.tronto.titiler.image.outgoing.port.ImageData
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.D3Array

internal class LongImageData(
    data: D3Array<Long>,
    mask: D2Array<Byte>,
    override val dataType: DataType,
) : ImageData, NDArrayImageData<Long>(data, mask) {
    companion object {
        @JvmStatic
        private val logger = KotlinLogging.logger { }
        private val availableTypes = listOf(
            DataType.UInt32,
            DataType.Int64
        )
    }

    init {
        require(dataType in availableTypes) {
            "dataType must be in $availableTypes"
        }
    }

    override fun Number.asType(): Long {
        return toLong()
    }

    override fun copy(data: D3Array<Long>, mask: D2Array<Byte>): NDArrayImageData<Long> {
        return LongImageData(data, mask, dataType)
    }
}
