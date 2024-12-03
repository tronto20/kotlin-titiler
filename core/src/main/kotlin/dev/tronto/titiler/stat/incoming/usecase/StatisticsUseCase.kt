package dev.tronto.titiler.stat.incoming.usecase

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.stat.domain.Statistics
import dev.tronto.titiler.stat.incoming.controller.option.StatisticsOption

interface StatisticsUseCase {
    suspend fun statistics(
        openOptions: OptionProvider<OpenOption>,
        imageOptions: OptionProvider<ImageOption>,
        statisticsOptions: OptionProvider<StatisticsOption>,
    ): Statistics
}
