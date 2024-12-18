package dev.tronto.titiler.spring.autoconfigure.webflux

import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.spring.autoconfigure.utils.sortedByOrdered
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import java.net.URI
import java.util.function.Supplier

@AutoConfiguration
@ConditionalOnClass(CorsWebFilter::class)
@EnableConfigurationProperties(TitilerWebProperties::class)
class TitilerWebAutoConfiguration(
    applicationContext: GenericApplicationContext,
) {
    init {
        OptionParser.services.forEach {
            applicationContext.defaultListableBeanFactory.registerBeanDefinition(
                it::class.qualifiedName ?: it.toString(),
                GenericBeanDefinition().apply {
                    beanClass = it::class.java
                    instanceSupplier = Supplier { it }
                }
            )
        }
    }

    @Bean
    fun webFluxOptionParserAdaptor(optionParsers: ObjectProvider<OptionParser<*>>): WebFluxOptionParserAdaptor {
        return WebFluxOptionParserAdaptor(optionParsers.sortedByOrdered())
    }

    @Bean
    fun corsWebFilter(titilerWebProperties: TitilerWebProperties): CorsWebFilter {
        val config = CorsConfiguration().apply {
            allowedOrigins = titilerWebProperties.cors.allowedOrigins
            allowedOriginPatterns = titilerWebProperties.cors.allowedOriginPatterns
            maxAge = titilerWebProperties.cors.maxAge
            allowedMethods = titilerWebProperties.cors.allowedMethods
            allowedHeaders = titilerWebProperties.cors.allowedHeaders
            exposedHeaders = titilerWebProperties.cors.exposedHeaders
            allowCredentials = titilerWebProperties.cors.allowCredentials
        }
        val source = UrlBasedCorsConfigurationSource().apply {
            val basePath = URI.create(titilerWebProperties.baseUri).path
            registerCorsConfiguration("$basePath/**", config)
        }

        return CorsWebFilter(source)
    }
}
