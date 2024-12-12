package dev.tronto.titiler.core.outgoing.adaptor.gdal.path

import dev.tronto.titiler.core.domain.Ordered
import java.net.URI

class DefaultCurlGdalPathProvider : GdalPathProvider, Ordered {
    companion object {
        private val ENABLED by lazy {
            System.getenv().run {
                !(get("TITILER_DISABLE_GDAL_PATH_CURL_DEFAULT")?.equals("YES", ignoreCase = true) ?: false)
            }
        }
    }
    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }

    override fun supports(uri: URI): Boolean {
        return ENABLED && uri.scheme in CurlGdalPath.SCHEME_LIST
    }

    override fun toGdalPath(uri: URI): GdalPath {
        return CurlGdalPath(uri)
    }
}
