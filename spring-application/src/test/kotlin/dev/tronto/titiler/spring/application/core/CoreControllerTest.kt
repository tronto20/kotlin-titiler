package dev.tronto.titiler.spring.application.core

import com.ninjasquad.springmockk.MockkBean
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.usecase.BoundsUseCase
import dev.tronto.titiler.core.incoming.usecase.InfoUseCase
import dev.tronto.titiler.spring.application.testAndDocument
import dev.tronto.titiler.spring.autoconfigure.core.TitilerCoreAutoConfiguration
import dev.tronto.titiler.spring.autoconfigure.core.TitilerCoreController
import dev.tronto.titiler.spring.autoconfigure.core.TitilerCorePathProperties
import dev.tronto.titiler.spring.autoconfigure.utils.sortedByOrdered
import dev.tronto.titiler.spring.autoconfigure.webflux.TitilerWebAutoConfiguration
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.coEvery
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.web.reactive.server.WebTestClient

@Import(TitilerCoreAutoConfiguration::class, TitilerWebAutoConfiguration::class)
@AutoConfigureRestDocs
@WebFluxTest(controllers = [TitilerCoreController::class])
@DisabledInAotMode
@DisabledInNativeImage
@MockkBean(BoundsUseCase::class, InfoUseCase::class)
class CoreControllerTest(
    private val webTestClient: WebTestClient,
    private val boundsUseCase: BoundsUseCase,
    private val infoUseCase: InfoUseCase,
    private val pathProperties: TitilerCorePathProperties,
    private val optionParsers: ObjectProvider<OptionParser<*>>,
) : FeatureSpec({
    val parsers: List<OptionParser<*>> = optionParsers.sortedByOrdered()
    feature("bounds") {
        scenario("Get Bounds") {
            coEvery { boundsUseCase.getBounds(any()) } returns testBounds

            webTestClient.testAndDocument(
                "bounds",
                "Return the bounds of the COG.",
                "Bounds",
                "Bounds",
                pathProperties.bounds,
                parsers,
                ArgumentType<OpenOption>(),
                responseFields = boundsFields
            )
        }
    }

    feature("info") {
        scenario("Get Info") {

            coEvery { infoUseCase.getInfo(any()) } returns testInfo

            webTestClient.testAndDocument(
                "info",
                "Return dataset's basic info.",
                "Info",
                "Info",
                pathProperties.info,
                parsers,
                ArgumentType<OpenOption>(),
                relaxedResponseFields = true,
                responseFields = infoFields
            )
        }
    }
})
