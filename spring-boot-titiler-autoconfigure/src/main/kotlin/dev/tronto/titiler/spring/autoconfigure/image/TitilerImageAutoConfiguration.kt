package dev.tronto.titiler.spring.autoconfigure.image

import dev.tronto.titiler.core.outgoing.adaptor.gdal.GdalRasterFactory
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.image.outgoing.adaptor.gdal.GdalReadableRasterFactory
import dev.tronto.titiler.image.outgoing.port.ReadableRasterFactory
import dev.tronto.titiler.image.service.ImageService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@AutoConfiguration
@ComponentScan
@EnableConfigurationProperties(TitilerImagePathProperties::class)
class TitilerImageAutoConfiguration {

    @Bean
    @ConditionalOnBean(GdalRasterFactory::class)
    @ConditionalOnMissingBean(ReadableRasterFactory::class)
    fun gdalReadableRasterFactory(crsFactory: CRSFactory, rasterFactory: GdalRasterFactory): GdalReadableRasterFactory =
        GdalReadableRasterFactory(
            crsFactory,
            rasterFactory
        )

    @Bean
    @ConditionalOnMissingBean(ReadableRasterFactory::class)
    fun gdalReadableRasterFactoryStandalone(crsFactory: CRSFactory): GdalReadableRasterFactory =
        GdalReadableRasterFactory(
            crsFactory,
            GdalRasterFactory(crsFactory)
        )

    @Bean
    fun imageService(crsFactory: CRSFactory, readableRasterFactory: ReadableRasterFactory): ImageService = ImageService(
        crsFactory,
        readableRasterFactory
    )
}
