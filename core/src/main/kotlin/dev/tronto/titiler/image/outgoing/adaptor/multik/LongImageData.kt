package dev.tronto.titiler.image.outgoing.adaptor.multik

import dev.tronto.titiler.core.domain.DataType
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.image.domain.ImageData
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.D3Array

class LongImageData(
    data: D3Array<Long>,
    mask: D2Array<Int>,
    override val dataType: DataType,
    vararg options: OptionProvider<*>,
) : ImageData, NDArrayImageData<Long>(data, mask, *options) {
    companion object {
        @JvmStatic
        private val logger = KotlinLogging.logger { }

        @JvmStatic
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

    override fun copy(
        data: D3Array<Long>,
        mask: D2Array<Int>,
        vararg options: OptionProvider<*>,
    ): NDArrayImageData<Long> {
        return LongImageData(data, mask, dataType, *options)
    }
}
