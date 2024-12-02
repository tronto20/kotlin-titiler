package dev.tronto.titiler.core.incoming.controller.option

import dev.tronto.titiler.core.exception.InvalidURIException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import java.net.URI

class URIOptionParser : OptionParser<URIOption> {
    companion object {
        const val PARAM = "uri"
    }

    override val type: ArgumentType<URIOption> = ArgumentType()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override suspend fun parse(request: Request): URIOption? {
        val uriString = request.parameter(PARAM).lastOrNull() ?: return null
        val uri = kotlin.runCatching { URI.create(uriString) }.getOrElse { throw InvalidURIException(uriString, it) }
        return URIOption(uri)
    }
}
