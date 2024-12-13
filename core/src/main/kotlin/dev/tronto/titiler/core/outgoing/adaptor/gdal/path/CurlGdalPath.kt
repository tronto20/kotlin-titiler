package dev.tronto.titiler.core.outgoing.adaptor.gdal.path

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import java.net.URI

class CurlGdalPath(
    private val uri: URI,
    override val openOptions: OptionProvider<OpenOption> = OptionProvider.empty(),
) : GdalPath {
    companion object {
        @JvmStatic
        val SCHEME_LIST = listOf("http", "https", "ftp")
    }
    init {
        require(uri.scheme in SCHEME_LIST)
    }
    override fun toURI(): URI {
        return uri
    }

    override fun toPathString(): String {
        return "/vsicurl/${this.uri}"
    }
}
