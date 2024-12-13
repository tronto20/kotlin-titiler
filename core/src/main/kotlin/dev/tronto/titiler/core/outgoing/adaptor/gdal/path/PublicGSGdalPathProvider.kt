package dev.tronto.titiler.core.outgoing.adaptor.gdal.path

import dev.tronto.titiler.core.domain.Ordered
import dev.tronto.titiler.core.incoming.controller.option.EnvOption
import dev.tronto.titiler.core.incoming.controller.option.OpenOption
import dev.tronto.titiler.core.incoming.controller.option.OptionProvider
import dev.tronto.titiler.core.incoming.controller.option.plus
import java.net.URI

class PublicGSGdalPathProvider : GdalPathProvider, Ordered {
    companion object {
        private val ENABLED by lazy {
            System.getenv().run {
                !(get("TITILER_DISABLE_GDAL_PATH_GS_PUBLIC")?.equals("YES", ignoreCase = true) ?: false)
            }
        }
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    override fun supports(uri: URI): Boolean {
        return ENABLED && uri.scheme == GSGdalPath.SCHEME
    }

    override fun toGdalPath(uri: URI): GdalPath {
        return GSGdalPath(uri, OptionProvider.empty<OpenOption>().plus(EnvOption("GS_NO_SIGN_REQUEST", "YES")))
    }
}
