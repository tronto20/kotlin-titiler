package dev.tronto.titiler.spring.application.core

data class CorsProperties(
    val allowedOrigins: List<String> = listOf(),
    val allowedOriginPatterns: List<String> = listOf(),
    val maxAge: Long = 3600,
    val allowedMethods: List<String> = listOf("GET", "POST"),
    val allowedHeaders: List<String> = listOf(),
    val exposedHeaders: List<String> = listOf(),
    val allowCredentials: Boolean = false,
)
