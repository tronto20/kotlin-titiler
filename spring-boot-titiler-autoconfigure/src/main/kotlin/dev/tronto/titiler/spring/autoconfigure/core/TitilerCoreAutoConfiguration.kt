package dev.tronto.titiler.spring.autoconfigure.core

import dev.tronto.titiler.core.outgoing.adaptor.gdal.GdalRasterFactory
import dev.tronto.titiler.core.outgoing.adaptor.gdal.SpatialReferenceCRSFactory
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import dev.tronto.titiler.core.service.CoreService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@AutoConfiguration
@ComponentScan
@EnableConfigurationProperties(TitilerCorePathProperties::class)
class TitilerCoreAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(CRSFactory::class)
    fun crsFactory(): CRSFactory = SpatialReferenceCRSFactory

    @Bean
    @ConditionalOnMissingBean(RasterFactory::class)
    fun rasterFactory(crsFactory: CRSFactory): RasterFactory = GdalRasterFactory(crsFactory)

    @Bean
    fun coreService(rasterFactory: RasterFactory): CoreService = CoreService(rasterFactory)
}
