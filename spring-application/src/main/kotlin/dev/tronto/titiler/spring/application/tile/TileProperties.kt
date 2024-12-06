package dev.tronto.titiler.spring.application.tile

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "titiler.tile")
data class TileProperties(
    val tileMatrixSetResourcePattern: String = "classpath*:tile/matrixset/*.json",
)
