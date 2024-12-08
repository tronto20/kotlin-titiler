package dev.tronto.titiler.stat.service

import dev.tronto.titiler.core.domain.BandIndex
import dev.tronto.titiler.core.domain.OptionContext
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.getOrNull
import dev.tronto.titiler.core.incoming.controller.option.plus
import dev.tronto.titiler.image.domain.ImageData
import dev.tronto.titiler.image.domain.ImageFormat
import dev.tronto.titiler.image.incoming.controller.option.BandIndexOption
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.ImageSizeOption
import dev.tronto.titiler.image.incoming.controller.option.MaxSizeOption
import dev.tronto.titiler.image.outgoing.port.ImageDataAutoRescale
import dev.tronto.titiler.stat.domain.Percentile
import dev.tronto.titiler.stat.incoming.controller.option.PercentileOption
import dev.tronto.titiler.stat.incoming.controller.option.StatisticsOption
import dev.tronto.titiler.stat.incoming.usecase.StatisticsUseCase

class StatisticsAutoRescale(
    private val statisticsUseCase: StatisticsUseCase = StatisticsService(),
) : ImageDataAutoRescale {

    override fun supports(imageData: ImageData, format: ImageFormat): Boolean {
        return (format == ImageFormat.PNG || format == ImageFormat.JPEG) && (imageData is OptionContext)
    }

    override suspend fun rescale(imageData: ImageData, format: ImageFormat): ImageData {
        require(supports(imageData, format) && imageData is OptionContext)
        val openOptions = imageData.getOptionProvider(ArgumentType<OpenOption>())
        val imageOptions = imageData.getOptionProvider(ArgumentType<ImageOption>())

        val filteredImageOptions = imageOptions
            .filterNot(ArgumentType<MaxSizeOption>())
            .filterNot(ArgumentType<ImageSizeOption>())
            .plus(MaxSizeOption(1024))

        val percentileRange = Percentile(2)..Percentile(98)
        val statisticsOptions = OptionProvider.empty<StatisticsOption>() +
            PercentileOption(listOf(percentileRange.start, percentileRange.endInclusive))

        val statistics = statisticsUseCase.statistics(
            openOptions,
            filteredImageOptions,
            statisticsOptions
        )

        val bandIndexOption: BandIndexOption? = imageOptions.getOrNull()
        val bandIndexes = bandIndexOption?.bandIndexes ?: (1..imageData.band).map { BandIndex(it) }

        val rangeFrom = bandIndexes.map { bandIndex ->
            val percentiles = statistics.statistics[bandIndex]!!.percentiles
            val start = percentiles[percentileRange.start]!!
            val end = percentiles[percentileRange.endInclusive]!!
            start..end
        }
        return imageData.rescaleToUInt8(rangeFrom)
    }
}
