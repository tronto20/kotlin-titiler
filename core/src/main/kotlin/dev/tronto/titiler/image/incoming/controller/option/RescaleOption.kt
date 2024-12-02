package dev.tronto.titiler.image.incoming.controller.option

@JvmInline
value class RescaleOption(
    val rescale: List<ClosedRange<Double>>,
) : ImageOption
