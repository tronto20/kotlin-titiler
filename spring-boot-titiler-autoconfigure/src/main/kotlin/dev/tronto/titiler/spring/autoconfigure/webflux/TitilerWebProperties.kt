package dev.tronto.titiler.spring.autoconfigure.webflux

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "titiler.web")
data class TitilerWebProperties(
    val baseUri: String = "http://localhost:8080",
)
