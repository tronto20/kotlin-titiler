package dev.tronto.titiler.spring.application.wmts

import dev.tronto.titiler.core.incoming.controller.option.filter
import dev.tronto.titiler.spring.application.core.GET
import dev.tronto.titiler.spring.application.core.adaptor.WebFluxOptionParserAdaptor
import dev.tronto.titiler.spring.application.core.contentType
import dev.tronto.titiler.wmts.incoming.usecase.WmtsUseCase
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@ConfigurationProperties(prefix = "titiler.web.path.wmts")
data class WmtsPathProperties(
    val capabilities: List<String> = listOf(
        "{tileMatrixSetId}/WMTSCapabilities.xml",
        "WMTSCapabilities.xml"
    ),
)

@Controller
class WmtsController(
    private val optionParser: WebFluxOptionParserAdaptor,
    private val wmtsPathProperties: WmtsPathProperties,
    private val wmtsUseCase: WmtsUseCase,
) : RouterFunction<ServerResponse> by coRouter({
    GET(wmtsPathProperties.capabilities) {
        val options = optionParser.parse(it)
        val capabilities = wmtsUseCase.wmts(
            options.filter(),
            options.filter(),
            options.filter(),
            options.filter(),
            options.filter()
        )
        ok().contentType(capabilities.format.contentType).bodyValueAndAwait(capabilities.contents)
    }
})
