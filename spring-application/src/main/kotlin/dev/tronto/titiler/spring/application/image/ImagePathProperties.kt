package dev.tronto.titiler.spring.application.image

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "titiler.web.path.image")
data class ImagePathProperties(
    val bbox: List<String> = listOf(
        "/bbox/{minx},{miny},{maxx},{maxy}/{width}x{height}.{format}",
        "/bbox/{minx},{miny},{maxx},{maxy}/{width}x{height}",
        "/bbox/{minx},{miny},{maxx},{maxy}.{format}",
        "/bbox/{minx},{miny},{maxx},{maxy}"
    ),
    val preview: List<String> = listOf(
        "/preview.{format}",
        "/preview"
    ),
)
