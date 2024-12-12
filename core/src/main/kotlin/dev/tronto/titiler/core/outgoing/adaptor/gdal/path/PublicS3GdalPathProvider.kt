package dev.tronto.titiler.core.outgoing.adaptor.gdal.path

import dev.tronto.titiler.core.domain.Ordered
import dev.tronto.titiler.core.incoming.controller.option.EnvOption
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.plus
import java.net.URI

class PublicS3GdalPathProvider : GdalPathProvider, Ordered {
    companion object {
        private val ENABLED by lazy {
            System.getenv().run {
                !(get("TITILER_DISABLE_GDAL_PATH_S3_PUBLIC")?.equals("YES", ignoreCase = true) ?: false)
            }
        }
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    override fun supports(uri: URI): Boolean {
        return ENABLED && uri.scheme == S3GdalPath.SCHEME
    }

    override fun toGdalPath(uri: URI): GdalPath {
        return S3GdalPath(uri, OptionProvider.empty<OpenOption>().plus(EnvOption("AWS_NO_SIGN_REQUEST", "YES")))
    }
}
