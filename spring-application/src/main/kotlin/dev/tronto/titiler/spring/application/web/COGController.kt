package dev.tronto.titiler.spring.application.web

import dev.tronto.titiler.core.incoming.controller.option.filter
import dev.tronto.titiler.core.incoming.usecase.BoundsUseCase
import dev.tronto.titiler.core.incoming.usecase.InfoUseCase
import dev.tronto.titiler.image.incoming.usecase.ImageBBoxUseCase
import dev.tronto.titiler.image.incoming.usecase.ImagePreviewUseCase
import dev.tronto.titiler.image.incoming.usecase.ImageRenderUseCase
import dev.tronto.titiler.spring.application.config.adaptor.WebFluxOptionParserAdaptor
import dev.tronto.titiler.stat.incoming.usecase.StatisticsUseCase
import dev.tronto.titiler.tile.incoming.usecase.TileInfoUseCase
import dev.tronto.titiler.tile.incoming.usecase.TileUseCase
import dev.tronto.titiler.wmts.incoming.usecase.WMTSUseCase
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
import java.io.ByteArrayInputStream

@Controller
class COGController(
    private val optionParser: WebFluxOptionParserAdaptor,
    private val boundsUseCase: BoundsUseCase,
    private val infoUseCase: InfoUseCase,
    private val tileUseCase: TileUseCase,
    private val tileInfoUseCase: TileInfoUseCase,
    private val imageBBoxUseCase: ImageBBoxUseCase,
    private val imagePreviewUseCase: ImagePreviewUseCase,
    private val imageRenderUseCase: ImageRenderUseCase,
    private val statisticsUseCase: StatisticsUseCase,
    private val wmtsUseCase: WMTSUseCase,
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
                val imageData = imageBBoxUseCase.bbox(options.filter(), options.filter())
                val image = imageRenderUseCase.renderImage(imageData, options.filter())
                ok().contentType(MediaType.parseMediaType(image.format.contentType))
                    .body(BodyInserters.fromResource(ByteArrayResource(image.data)))
                    .awaitSingle()
            }

            val previewPaths = listOf(
                "preview.{format}",
                "preview"
            )

            GET(previewPaths.map(RequestPredicates::path).reduce(RequestPredicate::or)) {
                val options = it.options()
                val imageData = imagePreviewUseCase.preview(options.filter(), options.filter())
                val image = imageRenderUseCase.renderImage(imageData, options.filter())
                ByteArrayInputStream(image.data).use { stream ->
                    val dataBuffers = DataBufferUtils.readInputStream(
                        { stream },
                        it.exchange().response.bufferFactory(),
                        2048
                    )
                    ok().contentType(MediaType.parseMediaType(image.format.contentType))
                        .contentLength(image.data.size.toLong())
                        .body(BodyInserters.fromDataBuffers(dataBuffers))
                        .awaitSingle()
                }
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
                    options.filter(),
                    options.filter()
                )

                ok().contentType(MediaType.parseMediaType(image.format.contentType))
                    .body(BodyInserters.fromResource(ByteArrayResource(image.data)))
                    .awaitSingle()
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

            GET("statistics") {
                val options = it.options()
                val statistics = statisticsUseCase.statistics(options.filter(), options.filter(), options.filter())
                ok().bodyValueAndAwait(statistics)
            }

            val wmtsPaths = listOf(
                "{tileMatrixSetId}/WMTSCapabilities.xml",
                "WMTSCapabilities.xml"
            )
            GET(wmtsPaths.map(RequestPredicates::path).reduce(RequestPredicate::or)) {
                val options = it.options()
                val document = wmtsUseCase.wmts(
                    options.filter(),
                    options.filter(),
                    options.filter(),
                    options.filter(),
                    options.filter()
                )
                ok().contentType(MediaType.parseMediaType(document.format.contentType))
                    .bodyValueAndAwait(document.contents)
            }
        }
    }
}
