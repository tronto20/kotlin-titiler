package dev.tronto.titiler.spring.autoconfigure.stat

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("titiler.web.path.stat")
data class TitilerStatPathProperties(
    val statistics: List<String> = listOf("/statistics"),
)
