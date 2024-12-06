package dev.tronto.titiler.spring.application.stat

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("titiler.web.path.stat")
data class StatPathProperties(
    val statistics: List<String> = listOf("/statistics"),
)
