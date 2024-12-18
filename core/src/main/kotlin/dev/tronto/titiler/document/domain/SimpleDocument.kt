package dev.tronto.titiler.document.domain

import dev.tronto.titiler.core.domain.OptionContext

data class SimpleDocument(override val contents: String, override val format: DocumentFormat) :
    Document,
    OptionContext by OptionContext.wrap()
