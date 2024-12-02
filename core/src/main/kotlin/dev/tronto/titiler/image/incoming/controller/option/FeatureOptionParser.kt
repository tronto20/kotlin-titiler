package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import org.locationtech.jts.geom.Polygon

class FeatureOptionParser : OptionParser<FeatureOption> {
    companion object {
        private const val DEFAULT_CRS = "EPSG:4326"
    }

    override val type: ArgumentType<FeatureOption> = ArgumentType()
    private val polygonType = ArgumentType<Polygon>()

    override fun generateMissingException(): Exception {
        return IllegalArgumentException("polygon body required.")
    }

    override suspend fun parse(request: Request): FeatureOption? {
        return request.body(polygonType)?.let {
            val crs = request.parameter("featureCrs").lastOrNull()
            FeatureOption(it, crs ?: DEFAULT_CRS)
        }
    }
}
