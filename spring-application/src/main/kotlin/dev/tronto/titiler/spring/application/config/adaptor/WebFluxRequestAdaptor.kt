package dev.tronto.titiler.spring.application.config.adaptor

import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.Request
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.server.ServerRequest

class WebFluxRequestAdaptor(
    val serverRequest: ServerRequest,
    val bodyKey: String? = null,
) : Request {
    private val queryParams = serverRequest.queryParams().mapKeys { it.key.lowercase() }
    private val pathParams = serverRequest.pathVariables().mapKeys { it.key.lowercase() }

    override suspend fun parameter(key: String): List<String> {
        val key = key.lowercase()
        val queryParameters = queryParams[key] ?: emptyList()
        return pathParams[key]?.let {
            queryParameters + it
        } ?: queryParameters
    }

    private val headers = serverRequest.headers().asHttpHeaders().mapKeys { it.key.lowercase() }

    override suspend fun option(key: String): List<String> {
        return headers[key.lowercase()] ?: emptyList()
    }

    override suspend fun <T : Any> body(key: String, argumentType: ArgumentType<T>): T? {
        return if (bodyKey == key) {
            serverRequest.body(
                BodyExtractors.toMono(ParameterizedTypeReference.forType<T>(argumentType.javaType))
            ).awaitSingleOrNull()
        } else {
            null
        }
    }
}
