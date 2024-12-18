package dev.tronto.titiler.spring.autoconfigure.wmts

import dev.tronto.titiler.core.incoming.controller.option.filter
import dev.tronto.titiler.spring.autoconfigure.webflux.GET
import dev.tronto.titiler.spring.autoconfigure.webflux.WebFluxOptionParserAdaptor
import dev.tronto.titiler.wmts.incoming.usecase.WmtsUseCase
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Controller
class TitilerWmtsController(
    private val optionParser: WebFluxOptionParserAdaptor,
    private val wmtsPathProperties: TitilerWmtsPathProperties,
    private val wmtsUseCase: WmtsUseCase,
) : RouterFunction<ServerResponse> by coRouter({
    GET(wmtsPathProperties.capabilities) {
        val options = optionParser.parse(it)
        val document = wmtsUseCase.wmts(
            options.filter(),
            options.filter(),
            options.filter(),
            options.filter()
        )
        ok().bodyValueAndAwait(document)
    }
})
