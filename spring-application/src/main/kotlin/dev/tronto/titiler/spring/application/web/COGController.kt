package dev.tronto.titiler.spring.application.web

import dev.tronto.titiler.core.incoming.usecase.BoundsUseCase
import dev.tronto.titiler.core.incoming.usecase.InfoUseCase
import dev.tronto.titiler.spring.application.config.adaptor.WebFluxOptionParserAdaptor
import dev.tronto.titiler.tile.incoming.usecase.TileInfoUseCase
import dev.tronto.titiler.tile.incoming.usecase.TileUseCase
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Controller
class COGController(
    private val optionParser: WebFluxOptionParserAdaptor = WebFluxOptionParserAdaptor(),
    private val boundsUseCase: BoundsUseCase,
    private val infoUseCase: InfoUseCase,
    private val tileUseCase: TileUseCase,
    private val tileInfoUseCase: TileInfoUseCase,
) {
    private suspend fun ServerRequest.options() = optionParser.parse(this)

    @Bean
    fun router() = coRouter {
        path("/cog").nest {
            GET("bounds") {
                ok().bodyValueAndAwait(
                    boundsUseCase.getBounds(it.options().filter())
                )
            }

            GET("info") {
                val options = optionParser.parse(it)
                ok().bodyValueAndAwait(
                    infoUseCase.getInfo(options.filter())
                )
            }

            val tilesPaths = listOf(
                "tiles/{tileMatrixSetId}/{z}/{x}/{y}@{scale}x.{format}",
                "tiles/{tileMatrixSetId}/{z}/{x}/{y}@{scale}x",
                "tiles/{tileMatrixSetId}/{z}/{x}/{y}.{format}",
                "tiles/{z}/{x}/{y}@{scale}x.{format}",
                "tiles/{z}/{x}/{y}.{format}",
                "tiles/{z}/{x}/{y}@{scale}x",
                "tiles/{z}/{x}/{y}"
            )

            GET(tilesPaths.map(RequestPredicates::path).reduce(RequestPredicate::or)) {
                val options = it.options()
                val data = tileUseCase.tile(
                    options.filter(),
                    options.filter(),
                    options.filter()
                )
                ok().contentType(MediaType.parseMediaType(data.format.contentType))
                    .bodyValueAndAwait(data.data)
            }

            val tileInfoPaths = listOf(
                "tiles/{tileMatrixSetId}/info",
                "tiles/info"
            )

            GET(tileInfoPaths.map(RequestPredicates::path).reduce(RequestPredicate::or)) {
                val options = it.options()
                val tileInfo = tileInfoUseCase.tileInfo(options.filter(), options.filter())
                ok().bodyValueAndAwait(tileInfo)
            }
        }
    }
}
