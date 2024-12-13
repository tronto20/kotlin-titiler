package dev.tronto.titiler.core.outgoing.adaptor.gdal.path

import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import java.net.URI

class S3GdalPath(
    private val uri: URI,
    override val openOptions: OptionProvider<OpenOption> = OptionProvider.empty(),
) : GdalPath {
    companion object {
        const val SCHEME = "s3"
    }
    init {
        require(uri.scheme == SCHEME)
    }
    override fun toURI(): URI {
        return uri
    }
    override fun toPathString(): String {
        return uri.toString().replace("$SCHEME://", "/vsis3/")
    }
}
