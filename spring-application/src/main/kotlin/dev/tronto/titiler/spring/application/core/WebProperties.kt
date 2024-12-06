package dev.tronto.titiler.spring.application.core

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "titiler.web")
data class WebProperties(
    val baseUri: String = "http://localhost:8080",
)
