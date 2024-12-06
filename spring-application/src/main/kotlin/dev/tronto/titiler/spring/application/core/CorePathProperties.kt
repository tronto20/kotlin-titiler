package dev.tronto.titiler.spring.application.core

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "titiler.web.path.core")
data class CorePathProperties(
    val bounds: List<String> = listOf("/bounds"),
    val info: List<String> = listOf("/info"),
)
