package dev.tronto.titiler.core.incoming.controller.option

import dev.tronto.titiler.core.exception.InvalidURIException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import java.net.URI

class URIOptionParser : OptionParser<URIOption> {
    companion object {
        private const val PARAM = "uri"
    }

    override val type: ArgumentType<URIOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override fun parse(request: Request): URIOption? {
        val uriString = request.parameter(PARAM).firstOrNull() ?: return null
        val uri = kotlin.runCatching { URI.create(uriString) }.getOrElse { throw InvalidURIException(uriString, it) }
        return URIOption(uri)
    }

    override fun box(option: URIOption): Map<String, List<String>> {
        return mapOf(PARAM to listOf(option.uri.toString()))
    }

    override fun descriptions(): List<OptionDescription<*>> {
        return listOf(OptionDescription<String>(PARAM, "target uri."))
    }
}
