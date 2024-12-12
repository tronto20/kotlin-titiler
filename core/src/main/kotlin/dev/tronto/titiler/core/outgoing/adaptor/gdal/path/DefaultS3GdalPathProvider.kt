package dev.tronto.titiler.core.outgoing.adaptor.gdal.path

import dev.tronto.titiler.core.domain.Ordered
import java.net.URI

class DefaultS3GdalPathProvider : GdalPathProvider, Ordered {
    companion object {
        private val ENABLED by lazy {
            System.getenv().run {
                !(get("TITILER_DISABLE_GDAL_PATH_S3_DEFAULT")?.equals("YES", ignoreCase = true) ?: false) ||
                    (containsKey("AWS_SECRET_ACCESS_KEY") && containsKey("AWS_ACCESS_KEY_ID")) ||
                        (containsKey("AWS_ROLE_ARN") && containsKey("AWS_WEB_IDENTITY_TOKEN_FILE"))
            }
        }
    }
    override fun getOrder(): Int {
        return Int.MAX_VALUE - 1
    }
    override fun supports(uri: URI): Boolean {
        return ENABLED && uri.scheme == S3GdalPath.SCHEME
    }

    override fun toGdalPath(uri: URI): GdalPath {
        return S3GdalPath(uri)
    }
}
