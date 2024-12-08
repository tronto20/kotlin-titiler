package dev.tronto.titiler.spring.application.tile

import dev.tronto.titiler.core.incoming.usecase.InfoUseCase
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import dev.tronto.titiler.image.incoming.usecase.ImageReadUseCase
import dev.tronto.titiler.tile.outgoing.adaptor.resource.ResourceTileMatrixSetFactory
import dev.tronto.titiler.tile.outgoing.port.TileMatrixSetFactory
import dev.tronto.titiler.tile.service.TileService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(TileProperties::class, TilePathProperties::class)
class TileConfiguration {

    @Bean
    fun tileMatrixSetFactory(properties: TileProperties) =
        ResourceTileMatrixSetFactory(properties.tileMatrixSetResourcePattern)

    @Bean
    fun tileService(
        tileMatrixSetFactory: TileMatrixSetFactory,
        crsFactory: CRSFactory,
        rasterFactory: RasterFactory,
        imageReadUseCase: ImageReadUseCase,
        infoUseCase: InfoUseCase,
    ) = TileService(
        tileMatrixSetFactory,
        crsFactory,
        rasterFactory,
        imageReadUseCase,
        infoUseCase
    )
}
