package dev.tronto.titiler.spring.application.config.adaptor

import dev.tronto.titiler.core.incoming.controller.option.Option
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import org.springframework.web.reactive.function.server.ServerRequest
import java.util.*

class WebFluxOptionParserAdaptor(
    private val optionParsers: Iterable<OptionParser<*>>,
) {
    constructor() : this(
        ServiceLoader.load(OptionParser::class.java, Thread.currentThread().contextClassLoader).toList()
    )

    suspend fun parse(request: ServerRequest): OptionProvider<Option> {
        val ex = WebFluxRequestAdaptor(request)
        return OptionProvider(optionParsers.mapNotNull { parser -> parser.parse(ex) }, optionParsers)
    }
}
