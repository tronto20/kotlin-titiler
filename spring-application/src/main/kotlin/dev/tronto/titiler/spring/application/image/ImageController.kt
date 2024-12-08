package dev.tronto.titiler.spring.application.image

import dev.tronto.titiler.core.incoming.controller.option.filter
import dev.tronto.titiler.image.incoming.usecase.ImageBBoxUseCase
import dev.tronto.titiler.image.incoming.usecase.ImagePreviewUseCase
import dev.tronto.titiler.image.incoming.usecase.ImageRenderUseCase
import dev.tronto.titiler.spring.application.core.GET
import dev.tronto.titiler.spring.application.core.adaptor.WebFluxOptionParserAdaptor
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Controller
class ImageController(
    private val optionParser: WebFluxOptionParserAdaptor,
    private val pathProperties: ImagePathProperties,
    private val bBoxUseCase: ImageBBoxUseCase,
    private val previewUseCase: ImagePreviewUseCase,
    private val renderUseCase: ImageRenderUseCase,
) : RouterFunction<ServerResponse> by coRouter({
    GET(pathProperties.bbox) {
        val options = optionParser.parse(it)
        val imageData = bBoxUseCase.bbox(options.filter(), options.filter())
        val image = renderUseCase.renderImage(imageData, options.filter())
        ok().bodyValueAndAwait(image)
    }

    GET(pathProperties.preview) {
        val options = optionParser.parse(it)
        val imageData = previewUseCase.preview(options.filter(), options.filter())
        val image = renderUseCase.renderImage(imageData, options.filter())
        ok().bodyValueAndAwait(image)
    }
})
