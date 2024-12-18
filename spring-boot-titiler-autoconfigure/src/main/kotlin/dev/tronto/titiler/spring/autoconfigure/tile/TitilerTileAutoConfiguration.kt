package dev.tronto.titiler.spring.autoconfigure.tile

import dev.tronto.titiler.core.incoming.usecase.InfoUseCase
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import dev.tronto.titiler.image.incoming.usecase.ImageReadUseCase
import dev.tronto.titiler.spring.autoconfigure.core.TitilerCoreAutoConfiguration
import dev.tronto.titiler.spring.autoconfigure.image.TitilerImageAutoConfiguration
import dev.tronto.titiler.tile.outgoing.adaptor.resource.ResourceTileMatrixSetFactory
import dev.tronto.titiler.tile.outgoing.port.TileMatrixSetFactory
import dev.tronto.titiler.tile.service.TileMatrixSetService
import dev.tronto.titiler.tile.service.TileService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@ComponentScan
@AutoConfiguration(after = [TitilerCoreAutoConfiguration::class, TitilerImageAutoConfiguration::class])
@EnableConfigurationProperties(TitilerTileProperties::class, TitilerTilePathProperties::class)
class TitilerTileAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TileMatrixSetFactory::class)
    fun tileMatrixSetFactory(properties: TitilerTileProperties) =
        ResourceTileMatrixSetFactory(properties.tileMatrixSetResourcePattern)

    @Bean
    @ConditionalOnBean(CRSFactory::class, RasterFactory::class, ImageReadUseCase::class, InfoUseCase::class)
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

    @Bean
    fun tileMatrixSetService(tileMatrixSetFactory: TileMatrixSetFactory) = TileMatrixSetService(
        tileMatrixSetFactory
    )
}
