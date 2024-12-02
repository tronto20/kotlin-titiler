package dev.tronto.titiler.core.incoming.controller.option

interface OptionParser<T : Option> {
    val type: ArgumentType<T>
    fun generateMissingException(): Exception

    suspend fun parse(request: Request): T?
}
