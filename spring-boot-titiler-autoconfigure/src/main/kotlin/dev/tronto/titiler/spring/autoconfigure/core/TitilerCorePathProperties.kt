package dev.tronto.titiler.spring.autoconfigure.core

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "titiler.web.path.core")
data class TitilerCorePathProperties(
    val bounds: List<String> = listOf("/bounds"),
    val info: List<String> = listOf("/info"),
)
