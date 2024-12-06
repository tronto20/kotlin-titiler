package dev.tronto.titiler.stat.outgoing.adaptor.multik

import dev.tronto.titiler.image.outgoing.adaptor.multik.NDArrayImageData
import dev.tronto.titiler.image.outgoing.port.ImageData
import dev.tronto.titiler.stat.domain.BandStatistics
import dev.tronto.titiler.stat.domain.Percentile
import dev.tronto.titiler.stat.outgoing.port.ImageDataStatistics
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.view
import org.jetbrains.kotlinx.multik.ndarray.operations.filterMultiIndexed
import org.jetbrains.kotlinx.multik.ndarray.operations.firstOrNull
import org.jetbrains.kotlinx.multik.ndarray.operations.groupNDArrayBy
import org.jetbrains.kotlinx.multik.ndarray.operations.lastOrNull
import org.jetbrains.kotlinx.multik.ndarray.operations.sorted
import org.jetbrains.kotlinx.multik.ndarray.operations.sum
import kotlin.math.pow
import kotlin.math.sqrt

class NDArrayImageDataStatistics : ImageDataStatistics {
    companion object {
        @JvmStatic
        private val logger = KotlinLogging.logger { }
    }

    override fun supports(imageData: ImageData): Boolean {
        return imageData is NDArrayImageData<*>
    }

    override suspend fun statistics(imageData: ImageData, percentiles: List<Percentile>): List<BandStatistics> {
        if (imageData !is NDArrayImageData<*>) {
            throw IllegalStateException("not supported.")
        }

        val maskedPixels = imageData.mask.size - imageData.mask.sum()
        return (0..<imageData.band).map { band ->
            CoroutineScope(Dispatchers.Default).async {
                val doubleData = (imageData.data.view(band) as D2Array<*>).asType<Double>()
                val data = doubleData.filterMultiIndexed { index, _ ->
                    imageData.mask[index] == 1
                }.sorted()
                val valueGroup = data.groupNDArrayBy { it }
                val min = data.firstOrNull() ?: 0.0
                val max = data.lastOrNull() ?: 0.0
                val sum = data.sum()
                val count = data.size
                val mean = sum / count
                val variance = valueGroup.map {
                    (mean - it.key).pow(2) * it.value.size
                }.sum() / count
                val std = sqrt(variance)
                val median = data[count / 2]

                val unique = valueGroup.keys.size

                var minorityValue = valueGroup.keys.firstOrNull() ?: 0.0
                var minoritySize = valueGroup.get(minorityValue)?.size ?: 0
                var majorityValue = valueGroup.keys.firstOrNull() ?: 0.0
                var majoritySize = valueGroup.get(majorityValue)?.size ?: 0

                valueGroup.mapValues { it.value.size }.toList()
                    .forEach { (value, size) ->
                        if (minoritySize > size) {
                            minoritySize = size
                            minorityValue = value
                        }
                        if (majoritySize < size) {
                            majoritySize = size
                            majorityValue = value
                        }
                    }

                @Suppress("UnnecessaryVariable")
                val validPixels = count

                val percentileMap = percentiles.associateWith {
                    data[data.size * it.value / 100]
                }

                BandStatistics(
                    min = min,
                    max = max,
                    mean = mean,
                    count = count,
                    sum = sum,
                    std = std,
                    median = median,
                    majority = majorityValue,
                    minority = minorityValue,
                    unique = unique,
                    validPercent = validPixels.toDouble() / (validPixels + maskedPixels) * 100,
                    maskedPixels = maskedPixels,
                    validPixels = validPixels,
                    percentiles = percentileMap
                )
            }
        }.awaitAll()
    }
}
