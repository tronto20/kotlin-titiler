package dev.tronto.titiler.spring.application.web

import dev.tronto.titiler.core.incoming.usecase.BoundsUseCase
import dev.tronto.titiler.core.incoming.usecase.InfoUseCase
import dev.tronto.titiler.image.incoming.usecase.ImageBBoxUseCase
import dev.tronto.titiler.spring.application.config.adaptor.WebFluxOptionParserAdaptor
import dev.tronto.titiler.tile.incoming.usecase.TileInfoUseCase
import dev.tronto.titiler.tile.incoming.usecase.TileUseCase
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.BodyInserters
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
    private val imageBBoxUseCase: ImageBBoxUseCase,
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
            val bboxPaths = listOf(
                "bbox/{minx},{miny},{maxx},{maxy}/{width}x{height}.{format}",
                "bbox/{minx},{miny},{maxx},{maxy}/{width}x{height}",
                "bbox/{minx},{miny},{maxx},{maxy}.{format}",
                "bbox/{minx},{miny},{maxx},{maxy}"
            )

            GET(bboxPaths.map(RequestPredicates::path).reduce(RequestPredicate::or)) {
                val options = it.options()
                val image = imageBBoxUseCase.bbox(options.filter(), options.filter())
                ok().contentType(MediaType.parseMediaType(image.format.contentType))
                    .body(BodyInserters.fromResource(ByteArrayResource(image.data)))
                    .awaitSingle()
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
                val image = tileUseCase.tile(
                    options.filter(),
                    options.filter(),
                    options.filter()
                )

                ok().contentType(MediaType.parseMediaType(image.format.contentType))
                    .bodyValueAndAwait(image.data)
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
