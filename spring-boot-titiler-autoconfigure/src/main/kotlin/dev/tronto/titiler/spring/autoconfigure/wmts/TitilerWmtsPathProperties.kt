package dev.tronto.titiler.spring.autoconfigure.wmts

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "titiler.web.path.wmts")
data class TitilerWmtsPathProperties(
    val capabilities: List<String> = listOf(
        "/{tileMatrixSetId}/WMTSCapabilities.xml",
        "/WMTSCapabilities.xml"
    ),
)
