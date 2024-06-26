package io.github.tronto20.titiler.param

import io.github.tronto20.titiler.domain.Percentile

data class StatisticsParam(
    val maxSize: Int? = null,
    val width: Int? = null,
    val height: Int? = null,
    val percentiles: List<Percentile> =
        listOf(Percentile(2), Percentile(98)),
)
