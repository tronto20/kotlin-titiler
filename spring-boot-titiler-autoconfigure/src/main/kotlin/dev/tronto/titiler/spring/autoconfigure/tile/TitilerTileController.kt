package dev.tronto.titiler.spring.autoconfigure.tile

import dev.tronto.titiler.core.incoming.controller.option.filter
import dev.tronto.titiler.image.incoming.usecase.ImageRenderUseCase
import dev.tronto.titiler.spring.autoconfigure.webflux.GET
import dev.tronto.titiler.spring.autoconfigure.webflux.WebFluxOptionParserAdaptor
import dev.tronto.titiler.tile.incoming.usecase.TileInfoUseCase
import dev.tronto.titiler.tile.incoming.usecase.TileMatrixSetUseCase
import dev.tronto.titiler.tile.incoming.usecase.TileUseCase
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Controller
class TitilerTileController(
    private val optionParser: WebFluxOptionParserAdaptor,
    private val pathProperties: TitilerTilePathProperties,
    private val infoUseCase: TileInfoUseCase,
    private val tileUseCase: TileUseCase,
    private val renderUseCase: ImageRenderUseCase,
    private val tileMatrixSetUseCase: TileMatrixSetUseCase,
) : RouterFunction<ServerResponse> by coRouter({
    GET(pathProperties.info) {
        val options = optionParser.parse(it)
        val info = infoUseCase.tileInfo(options.filter(), options.filter())
        ok().bodyValueAndAwait(info)
    }

    GET(pathProperties.tiles) {
        val options = optionParser.parse(it)
        val tile = tileUseCase.tile(options.filter(), options.filter())
        val image = renderUseCase.renderImage(tile, options.filter())
        ok().bodyValueAndAwait(image)
    }

    GET(pathProperties.tileMatrixSets) {
        ok().bodyValueAndAwait(tileMatrixSetUseCase.tileMatrixSets())
    }

    GET(pathProperties.tileMatrixSet) {
        val options = optionParser.parse(it)
        ok().bodyValueAndAwait(tileMatrixSetUseCase.tileMatrixSet(options.filter()))
    }
})
