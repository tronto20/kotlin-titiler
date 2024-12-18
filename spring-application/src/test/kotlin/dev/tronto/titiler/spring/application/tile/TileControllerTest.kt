package dev.tronto.titiler.spring.application.tile

import com.ninjasquad.springmockk.MockkBean
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.image.incoming.controller.option.RenderOption
import dev.tronto.titiler.image.incoming.usecase.ImageRenderUseCase
import dev.tronto.titiler.spring.application.image.testImage
import dev.tronto.titiler.spring.application.image.testImageData
import dev.tronto.titiler.spring.application.testAndDocument
import dev.tronto.titiler.spring.autoconfigure.core.TitilerCoreAutoConfiguration
import dev.tronto.titiler.spring.autoconfigure.image.TitilerImageRenderAutoConfiguration
import dev.tronto.titiler.spring.autoconfigure.tile.TitilerTileAutoConfiguration
import dev.tronto.titiler.spring.autoconfigure.tile.TitilerTileController
import dev.tronto.titiler.spring.autoconfigure.tile.TitilerTilePathProperties
import dev.tronto.titiler.spring.autoconfigure.utils.sortedByOrdered
import dev.tronto.titiler.spring.autoconfigure.webflux.TitilerWebAutoConfiguration
import dev.tronto.titiler.tile.incoming.controller.option.TileMatrixSetOption
import dev.tronto.titiler.tile.incoming.controller.option.TileOption
import dev.tronto.titiler.tile.incoming.usecase.TileInfoUseCase
import dev.tronto.titiler.tile.incoming.usecase.TileMatrixSetUseCase
import dev.tronto.titiler.tile.incoming.usecase.TileUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.coEvery
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.web.reactive.server.WebTestClient

@Import(
    TitilerWebAutoConfiguration::class,
    TitilerCoreAutoConfiguration::class,
    TitilerTileAutoConfiguration::class,
    TitilerImageRenderAutoConfiguration::class
)
@DisabledInAotMode
@DisabledInNativeImage
@AutoConfigureRestDocs
@WebFluxTest(controllers = [TitilerTileController::class])
@MockkBean(TileInfoUseCase::class, TileUseCase::class, ImageRenderUseCase::class, TileMatrixSetUseCase::class)
class TileControllerTest(
    private val webTestClient: WebTestClient,
    private val optionParsers: ObjectProvider<OptionParser<*>>,
    private val pathProperties: TitilerTilePathProperties,
    private val infoUseCase: TileInfoUseCase,
    private val tileUseCase: TileUseCase,
    private val renderUseCase: ImageRenderUseCase,
    private val tileMatrixSetUseCase: TileMatrixSetUseCase,
) : FeatureSpec({
    val parsers = optionParsers.sortedByOrdered()

    feature("tile info") {
        scenario("Get TileInfo") {
            coEvery { infoUseCase.tileInfo(any(), any()) } returns testTileInfo

            webTestClient.testAndDocument(
                "tileInfo",
                "Return dataset's tile info.",
                "TileInfo",
                "TileInfo",
                pathProperties.info,
                parsers,
                ArgumentType<OpenOption>(),
                ArgumentType<TileOption>(),
                responseFields = tileInfoFields
            )
        }
    }

    feature("tile") {
        scenario("Get Tile") {
            coEvery { tileUseCase.tile(any(), any()) } returns testImageData
            coEvery { renderUseCase.renderImage(testImageData, any()) } returns testImage

            webTestClient.testAndDocument(
                "tile",
                "Create map tile from a dataset.",
                "Tile",
                "Image",
                pathProperties.tiles,
                parsers,
                ArgumentType<OpenOption>(),
                ArgumentType<TileOption>(),
                ArgumentType<RenderOption>()
            )
        }
    }

    feature("tileMatrixSet") {
        scenario("List TileMatrixSets") {
            coEvery { tileMatrixSetUseCase.tileMatrixSets() } returns listOf(testTileMatrixSet)

            webTestClient.testAndDocument(
                "tileMatrixSets",
                "list TileMatrixSet.",
                "TileMatrixSet",
                "TileMatrixSetList",
                pathProperties.tileMatrixSets,
                parsers,
                tags = listOf("tileMatrixSet")
            )
        }

        scenario("Get TileMatrixSet") {
            coEvery { tileMatrixSetUseCase.tileMatrixSet(any()) } returns testTileMatrixSet

            webTestClient.testAndDocument(
                "tileMatrixSet",
                "Get TileMatrixSet by id.",
                "TileMatrixSet",
                "TileMatrixSet",
                pathProperties.tileMatrixSet,
                parsers,
                ArgumentType<TileMatrixSetOption>()
            )
        }
    }
})
