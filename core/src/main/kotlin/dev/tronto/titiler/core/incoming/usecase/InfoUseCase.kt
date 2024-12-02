package dev.tronto.titiler.core.incoming.usecase

import dev.tronto.titiler.core.domain.Info
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider

interface InfoUseCase {
    suspend fun getInfo(openOptions: OptionProvider<OpenOption>): Info
}
