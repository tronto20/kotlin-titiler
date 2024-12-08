package dev.tronto.titiler.spring.application.wmts

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "titiler.web.path.wmts")
data class WmtsPathProperties(
    val capabilities: List<String> = listOf(
        "{tileMatrixSetId}/WMTSCapabilities.xml",
        "WMTSCapabilities.xml"
    ),
)
