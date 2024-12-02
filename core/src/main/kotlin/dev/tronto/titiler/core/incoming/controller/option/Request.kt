package dev.tronto.titiler.core.incoming.controller.option

interface Request {
    suspend fun parameter(key: String): List<String>

    suspend fun option(key: String): List<String>

    suspend fun <T : Any> body(argumentType: ArgumentType<T>): T?
}
