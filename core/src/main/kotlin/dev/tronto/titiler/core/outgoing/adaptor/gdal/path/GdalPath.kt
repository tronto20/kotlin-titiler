package dev.tronto.titiler.core.outgoing.adaptor.gdal.path

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import java.net.URI

interface GdalPath {
    fun toURI(): URI
    fun toPathString(): String
    val openOptions: OptionProvider<OpenOption>
}
