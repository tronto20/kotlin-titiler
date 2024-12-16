package dev.tronto.titiler.spring.application.core

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter

@Configuration
@EnableConfigurationProperties(WebProperties::class)
class WebConfiguration {
    @Bean
    fun corsWebFilter(webProperties: WebProperties): CorsWebFilter {
        val config = CorsConfiguration().apply {
            allowedOrigins = webProperties.cors.allowedOrigins
            allowedOriginPatterns = webProperties.cors.allowedOriginPatterns
            maxAge = webProperties.cors.maxAge
            allowedMethods = webProperties.cors.allowedMethods
            allowedHeaders = webProperties.cors.allowedHeaders
            exposedHeaders = webProperties.cors.exposedHeaders
            allowCredentials = webProperties.cors.allowCredentials
        }

        return CorsWebFilter { config }
    }
}
