package dev.tronto.titiler.spring.application.stat

import com.ninjasquad.springmockk.MockkBean
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.spring.application.testAndDocument
import dev.tronto.titiler.spring.autoconfigure.core.TitilerCoreAutoConfiguration
import dev.tronto.titiler.spring.autoconfigure.stat.TitilerStatAutoConfiguration
import dev.tronto.titiler.spring.autoconfigure.stat.TitilerStatController
import dev.tronto.titiler.spring.autoconfigure.stat.TitilerStatPathProperties
import dev.tronto.titiler.spring.autoconfigure.utils.sortedByOrdered
import dev.tronto.titiler.spring.autoconfigure.webflux.TitilerWebAutoConfiguration
import dev.tronto.titiler.stat.incoming.controller.option.StatisticsOption
import dev.tronto.titiler.stat.incoming.usecase.StatisticsUseCase
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.coEvery
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.web.reactive.server.WebTestClient

@Import(TitilerWebAutoConfiguration::class, TitilerCoreAutoConfiguration::class, TitilerStatAutoConfiguration::class)
@DisabledInAotMode
@DisabledInNativeImage
@AutoConfigureRestDocs
@WebFluxTest(controllers = [TitilerStatController::class])
@MockkBean(StatisticsUseCase::class)
class StatControllerTest(private val webTestClient: WebTestClient, private val optionParsers: ObjectProvider<OptionParser<*>>, private val pathProperties: TitilerStatPathProperties, private val statisticsUseCase: StatisticsUseCase) :
    FeatureSpec({
        val parsers = optionParsers.sortedByOrdered()
        feature("statistics") {
            scenario("Get Statistics") {

                coEvery { statisticsUseCase.statistics(any(), any(), any()) } returns testStatistics

                webTestClient.testAndDocument(
                    "statistics",
                    "Get Dataset statistics.",
                    "Statistics",
                    "Statistics",
                    pathProperties.statistics,
                    parsers,
                    ArgumentType<OpenOption>(),
                    ArgumentType<ImageOption>(),
                    ArgumentType<StatisticsOption>(),
                    responseFields = statisticsFields
                )
            }
        }
    })
