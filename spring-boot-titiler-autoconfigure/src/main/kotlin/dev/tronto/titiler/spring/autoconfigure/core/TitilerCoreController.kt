package dev.tronto.titiler.spring.autoconfigure.core

import dev.tronto.titiler.core.incoming.controller.option.filter
import dev.tronto.titiler.core.incoming.usecase.BoundsUseCase
import dev.tronto.titiler.core.incoming.usecase.InfoUseCase
import dev.tronto.titiler.spring.autoconfigure.webflux.GET
import dev.tronto.titiler.spring.autoconfigure.webflux.WebFluxOptionParserAdaptor
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Controller
class TitilerCoreController(
    private val optionParser: WebFluxOptionParserAdaptor,
    private val titilerCorePathProperties: TitilerCorePathProperties,
    private val boundsUseCase: BoundsUseCase,
    private val infoUseCase: InfoUseCase,
) : RouterFunction<ServerResponse> by coRouter({
    GET(titilerCorePathProperties.bounds) {
        val options = optionParser.parse(it)
        val bounds = boundsUseCase.getBounds(options.filter())
        ok().bodyValueAndAwait(bounds)
    }

    GET(titilerCorePathProperties.info) {
        val options = optionParser.parse(it)
        val info = infoUseCase.getInfo(options.filter())
        ok().bodyValueAndAwait(info)
    }
})
