package dev.tronto.titiler.spring.application.core

import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.outgoing.adaptor.gdal.GdalRasterFactory
import dev.tronto.titiler.core.outgoing.adaptor.gdal.SpatialReferenceCRSFactory
import dev.tronto.titiler.core.outgoing.port.CRSFactory
import dev.tronto.titiler.core.outgoing.port.RasterFactory
import dev.tronto.titiler.core.service.CoreService
import dev.tronto.titiler.spring.application.core.adaptor.WebFluxOptionParserAdaptor
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.support.GenericApplicationContext
import java.util.*

@Configuration
@Import(WebConfiguration::class)
@EnableConfigurationProperties(CorePathProperties::class)
class CoreConfiguration(
    applicationContext: GenericApplicationContext,
) {
    init {
        OptionParser.services.forEach {
            applicationContext.defaultListableBeanFactory.registerBeanDefinition(
                it::class.qualifiedName ?: it.toString(),
                GenericBeanDefinition().apply {
                    setBeanClass(it::class.java)
                    setInstanceSupplier { it }
                }
            )
        }
    }

    @Bean
    fun webFluxOptionParserAdaptor(optionParsers: ObjectProvider<OptionParser<*>>): WebFluxOptionParserAdaptor {
        return WebFluxOptionParserAdaptor(optionParsers.sortedByOrdered())
    }

    @Bean
    fun crsFactory(): CRSFactory = SpatialReferenceCRSFactory

    @Bean
    fun rasterFactory(crsFactory: CRSFactory): RasterFactory = GdalRasterFactory(crsFactory)

    @Bean
    fun coreService(rasterFactory: RasterFactory): CoreService = CoreService(rasterFactory)
}
