package dev.tronto.titiler.spring.application.core

import dev.tronto.titiler.core.outgoing.adaptor.gdal.GdalRasterFactory
import dev.tronto.titiler.core.outgoing.adaptor.gdal.SpatialReferenceCRSFactory
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import dev.tronto.titiler.core.service.CoreService
import dev.tronto.titiler.spring.application.core.adaptor.WebFluxOptionParserAdaptor
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(WebProperties::class, CorePathProperties::class)
class CoreConfiguration {
    @Bean
    fun webFluxOptionParserAdaptor(): WebFluxOptionParserAdaptor = WebFluxOptionParserAdaptor()

    @Bean
    fun crsFactory(): CRSFactory = SpatialReferenceCRSFactory

    @Bean
    fun rasterFactory(crsFactory: CRSFactory): RasterFactory = GdalRasterFactory(crsFactory)

    @Bean
    fun coreService(rasterFactory: RasterFactory): CoreService = CoreService(rasterFactory)
}
