package dev.tronto.titiler.core.incoming.controller.option

import dev.tronto.titiler.core.domain.ResamplingAlgorithm

@JvmInline
value class ResamplingOption(val algorithm: ResamplingAlgorithm) : OpenOption
