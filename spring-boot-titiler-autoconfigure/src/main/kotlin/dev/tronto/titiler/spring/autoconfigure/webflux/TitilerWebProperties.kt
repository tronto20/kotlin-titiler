package dev.tronto.titiler.spring.autoconfigure.webflux

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "titiler.web")
data class TitilerWebProperties(
    val baseUri: String = "http://localhost:8080",
    @NestedConfigurationProperty
    val cors: TitilerCorsProperties = TitilerCorsProperties(),
)
