package dev.tronto.titiler.spring.application.image

import com.ninjasquad.springmockk.MockkBean
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.image.incoming.controller.option.ImageOption
import dev.tronto.titiler.image.incoming.controller.option.RenderOption
import dev.tronto.titiler.image.incoming.usecase.ImageBBoxUseCase
import dev.tronto.titiler.image.incoming.usecase.ImagePreviewUseCase
import dev.tronto.titiler.image.incoming.usecase.ImageRenderUseCase
import dev.tronto.titiler.spring.application.core.CoreConfiguration
import dev.tronto.titiler.spring.application.core.sortedByOrdered
import dev.tronto.titiler.spring.application.testAndDocument
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.coEvery
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.web.reactive.server.WebTestClient

@Import(CoreConfiguration::class, ImageConfiguration::class, ImageRenderConfiguration::class)
@DisabledInAotMode
@DisabledInNativeImage
@AutoConfigureRestDocs
@WebFluxTest(controllers = [ImageController::class])
@MockkBean(ImageBBoxUseCase::class, ImagePreviewUseCase::class, ImageRenderUseCase::class)
class ImageControllerTest(
    private val webTestClient: WebTestClient,
    private val optionParsers: ObjectProvider<OptionParser<*>>,
    private val pathProperties: ImagePathProperties,
    private val bBoxUseCase: ImageBBoxUseCase,
    private val previewUseCase: ImagePreviewUseCase,
    private val renderUseCase: ImageRenderUseCase,
) : FeatureSpec({
    val parsers: List<OptionParser<*>> = optionParsers.sortedByOrdered()

    feature("bbox") {
        scenario("Get BBox") {
            coEvery { bBoxUseCase.bbox(any(), any()) } returns testImageData
            coEvery { renderUseCase.renderImage(testImageData, any()) } returns testImage

            webTestClient.testAndDocument(
                "bbox",
                "Create image from a bbox.",
                "BBox Image",
                "Image",
                pathProperties.bbox,
                parsers,
                ArgumentType<OpenOption>(),
                ArgumentType<ImageOption>(),
                ArgumentType<RenderOption>()
            )
        }
    }

    feature("preview") {
        scenario("Get Preview") {
            coEvery { previewUseCase.preview(any(), any()) } returns testImageData
            coEvery { renderUseCase.renderImage(testImageData, any()) } returns testImage

            webTestClient.testAndDocument(
                "preview",
                "Create preview of a dataset.",
                "Preview",
                "Image",
                pathProperties.preview,
                parsers,
                ArgumentType<OpenOption>(),
                ArgumentType<ImageOption>(),
                ArgumentType<RenderOption>()
            )
        }
    }
})
