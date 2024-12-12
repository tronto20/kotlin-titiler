package dev.tronto.titiler.core.outgoing.adaptor.gdal.path

import dev.tronto.titiler.core.domain.Ordered
import java.net.URI
import java.util.*

interface GdalPathProvider {
    fun supports(uri: URI): Boolean
    fun toGdalPath(uri: URI): GdalPath

    companion object {
        @JvmStatic
        val services by lazy {
            ServiceLoader.load(GdalPathProvider::class.java, Thread.currentThread().contextClassLoader)
                .toList().sortedBy { if (it is Ordered) it.order else 0 }
        }

        fun of(uri: URI): GdalPath? {
            return services.find { it.supports(uri) }?.toGdalPath(uri)
        }
    }
}
