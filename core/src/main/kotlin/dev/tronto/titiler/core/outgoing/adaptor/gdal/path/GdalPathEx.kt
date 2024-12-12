package dev.tronto.titiler.core.outgoing.adaptor.gdal.path

import java.net.URI

fun URI.tryToGdalPath(): GdalPath? {
    return GdalPathProvider.of(this)
}
