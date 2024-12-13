package dev.tronto.titiler.core.outgoing.adaptor.gdal.path

import dev.tronto.titiler.core.domain.Ordered
import java.net.URI

class DefaultGSGdalPathProvider : GdalPathProvider, Ordered {
    companion object {
        private val ENABLED by lazy {
            System.getenv().run {
                !(get("TITILER_DISABLE_GDAL_PATH_GS_DEFAULT")?.equals("YES", ignoreCase = true) ?: false) ||
                    (get("GS_NO_SIGN_REQUEST")?.equals("YES", ignoreCase = true) ?: false) ||
                    (containsKey("GS_SECRET_ACCESS_KEY") && containsKey("GS_ACCESS_KEY_ID")) ||
                    containsKey("GDAL_HTTP_HEADER_FILE") ||
                    containsKey("GDAL_HTTP_HEADERS") ||
                    containsKey("GS_OAUTH2_REFRESH_TOKEN") ||
                    containsKey("GOOGLE_APPLICATION_CREDENTIALS") ||
                    (
                        (containsKey("GS_OAUTH2_PRIVATE_KEY") || containsKey("GS_OAUTH2_PRIVATE_KEY_FILE")) &&
                            containsKey("GS_OAUTH2_CLIEN_EMAIL")
                        ) ||
                    containsKey("GS_OAUTH2_CLIENT_EMAIL") ||
                    containsKey("GS_NO_SIGN_REQUEST") ||
                    containsKey("CPL_MACHINE_IS_GCE")
            }
        }
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE - 1
    }

    override fun supports(uri: URI): Boolean {
        return ENABLED && uri.scheme == GSGdalPath.SCHEME
    }

    override fun toGdalPath(uri: URI): GdalPath {
        return GSGdalPath(uri)
    }
}