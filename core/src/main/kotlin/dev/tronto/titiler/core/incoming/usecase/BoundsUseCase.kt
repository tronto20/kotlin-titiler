package dev.tronto.titiler.core.incoming.usecase

import dev.tronto.titiler.core.domain.Bounds
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider

interface BoundsUseCase {
    suspend fun getBounds(openOptions: OptionProvider<OpenOption>): Bounds
}
