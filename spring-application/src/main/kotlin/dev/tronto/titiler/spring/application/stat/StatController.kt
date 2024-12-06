package dev.tronto.titiler.spring.application.stat

import dev.tronto.titiler.core.incoming.controller.option.filter
import dev.tronto.titiler.spring.application.core.GET
import dev.tronto.titiler.spring.application.core.adaptor.WebFluxOptionParserAdaptor
import dev.tronto.titiler.stat.incoming.usecase.StatisticsUseCase
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Controller
class StatController(
    private val optionParser: WebFluxOptionParserAdaptor,
    private val pathProperties: StatPathProperties,
    private val statisticsUseCase: StatisticsUseCase,
) : RouterFunction<ServerResponse> by coRouter({
    GET(pathProperties.statistics) {
        val options = optionParser.parse(it)
        val statistics = statisticsUseCase.statistics(
            options.filter(),
            options.filter(),
            options.filter()
        )
        ok().bodyValueAndAwait(statistics)
    }
})
