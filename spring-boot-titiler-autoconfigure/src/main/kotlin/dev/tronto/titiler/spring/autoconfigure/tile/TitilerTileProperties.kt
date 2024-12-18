package dev.tronto.titiler.spring.autoconfigure.tile

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "titiler.tile")
data class TitilerTileProperties(val tileMatrixSetResourcePattern: String = "classpath*:tile/matrixset/*.json")
