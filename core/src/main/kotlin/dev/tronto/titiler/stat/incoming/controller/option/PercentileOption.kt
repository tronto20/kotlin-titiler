package dev.tronto.titiler.stat.incoming.controller.option

import dev.tronto.titiler.stat.domain.Percentile

@JvmInline
value class PercentileOption(
    val percentiles: List<Percentile>,
) : StatisticsOption
