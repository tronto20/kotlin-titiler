package dev.tronto.titiler.spring.application.core

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "titiler.web")
data class WebProperties(
    val baseUri: String = "http://localhost:8080",
    @NestedConfigurationProperty
    val cors: CorsProperties = CorsProperties(),
)
