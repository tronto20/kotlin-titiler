package dev.tronto.titiler.spring.application.wmts

import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.spring.application.core.WebProperties
import dev.tronto.titiler.spring.application.tile.TilePathProperties
import dev.tronto.titiler.tile.incoming.usecase.TileInfoUseCase
import dev.tronto.titiler.tile.outgoing.port.TileMatrixSetFactory
import dev.tronto.titiler.wmts.service.TemplateString
import dev.tronto.titiler.wmts.service.WmtssService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.TemplateEngine

@Configuration
@EnableConfigurationProperties(WmtsPathProperties::class)
class WmtsConfiguration {

    @Bean
    @ConditionalOnProperty("titiler.web.path.tile.tiles", "titiler.web.path.wmts.capabilities", matchIfMissing = true)
    fun wmtsService(
        templateEngine: TemplateEngine,
        crsFactory: CRSFactory,
        tileMatrixSetFactory: TileMatrixSetFactory,
        tileInfoUseCase: TileInfoUseCase,
        webProperties: WebProperties,
        tilePathProperties: TilePathProperties,
        wmtsPathProperties: WmtsPathProperties,
    ) = WmtssService(
        wmtsUriTemplate = TemplateString(
            webProperties.baseUri.trimEnd('/') + '/' +
                wmtsPathProperties.capabilities.first().trimStart('/')
        ),
        tilesUriTemplate = TemplateString(
            webProperties.baseUri.trimEnd('/') + '/' +
                tilePathProperties.tiles.first().trimStart('/')
        ),
        templateEngine = templateEngine,
        crsFactory = crsFactory,
        tileMatrixSetFactory = tileMatrixSetFactory,
        tileInfoUseCase = tileInfoUseCase
    )
}
