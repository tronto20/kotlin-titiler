package dev.tronto.titiler.spring.autoconfigure.wmts

import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.spring.autoconfigure.document.TitilerDocumentAutoConfiguration
import dev.tronto.titiler.spring.autoconfigure.tile.TitilerTilePathProperties
import dev.tronto.titiler.spring.autoconfigure.webflux.TitilerWebProperties
import dev.tronto.titiler.tile.incoming.usecase.TileInfoUseCase
import dev.tronto.titiler.tile.outgoing.port.TileMatrixSetFactory
import dev.tronto.titiler.wmts.service.TemplateString
import dev.tronto.titiler.wmts.service.WmtsService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.thymeleaf.TemplateEngine

@ComponentScan
@AutoConfiguration(after = [TitilerDocumentAutoConfiguration::class])
@EnableConfigurationProperties(TitilerWmtsPathProperties::class)
class TitilerWmtsAutoConfiguration {

    @Bean
    @ConditionalOnBean
    fun wmtsService(
        templateEngine: TemplateEngine,
        crsFactory: CRSFactory,
        tileMatrixSetFactory: TileMatrixSetFactory,
        tileInfoUseCase: TileInfoUseCase,
        webProperties: TitilerWebProperties,
        tilePathProperties: TitilerTilePathProperties,
        wmtsPathProperties: TitilerWmtsPathProperties,
    ) = WmtsService(
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
