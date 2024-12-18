package dev.tronto.titiler.spring.autoconfigure.webflux

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "titiler.web.cors")
data class TitilerCorsProperties(
    val allowedOrigins: List<String> = listOf(),
    val allowedOriginPatterns: List<String> = listOf(),
    val maxAge: Long = 3600,
    val allowedMethods: List<String> = listOf("GET", "POST"),
    val allowedHeaders: List<String> = listOf(),
    val exposedHeaders: List<String> = listOf(),
    val allowCredentials: Boolean = false,
)
