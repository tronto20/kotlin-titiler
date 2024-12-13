package dev.tronto.titiler.spring.application.wmts

import com.ninjasquad.springmockk.MockkBean
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.document.domain.Document
import dev.tronto.titiler.document.domain.DocumentFormat
import dev.tronto.titiler.image.incoming.controller.option.RenderOption
import dev.tronto.titiler.spring.application.core.CoreConfiguration
import dev.tronto.titiler.spring.application.core.sortedByOrdered
import dev.tronto.titiler.spring.application.testAndDocument
import dev.tronto.titiler.tile.incoming.controller.option.TileOption
import dev.tronto.titiler.wmts.incoming.controller.option.WmtsOption
import dev.tronto.titiler.wmts.incoming.usecase.WmtsUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.coEvery
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.web.reactive.server.WebTestClient

@Import(CoreConfiguration::class, WmtsConfiguration::class)
@DisabledInAotMode
@DisabledInNativeImage
@AutoConfigureRestDocs
@WebFluxTest(controllers = [WmtsController::class])
@MockkBean(WmtsUseCase::class)
class WmtsControllerTest(
    private val webTestClient: WebTestClient,
    private val optionParsers: ObjectProvider<OptionParser<*>>,
    private val pathProperties: WmtsPathProperties,
    private val wmtsUseCase: WmtsUseCase,
) : FeatureSpec({
    val parsers = optionParsers.sortedByOrdered()
    feature("wmts capabilities") {
        scenario("Get WMTSCapabilities") {
            coEvery { wmtsUseCase.wmts(any(), any(), any(), any()) } returns object : Document {
                override val contents: String
                    get() = "contents"
                override val format: DocumentFormat
                    get() = DocumentFormat.XML
            }

            webTestClient.testAndDocument(
                "wmtsCapabilities",
                "OGC WMTS endpoint.",
                "Wmts",
                "WMTSCapabilities",
                pathProperties.capabilities,
                parsers,
                ArgumentType<OpenOption>(),
                ArgumentType<TileOption>(),
                ArgumentType<RenderOption>(),
                ArgumentType<WmtsOption>()
            )
        }
    }
})
