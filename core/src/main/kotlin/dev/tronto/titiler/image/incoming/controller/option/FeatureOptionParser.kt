package dev.tronto.titiler.image.incoming.controller.option

import dev.tronto.titiler.core.exception.IllegalParameterException
import dev.tronto.titiler.core.exception.RequiredParameterMissingException
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.core.incoming.controller.option.Request
import org.locationtech.jts.geom.Polygon

class FeatureOptionParser : OptionParser<FeatureOption> {
    companion object {
        private const val DEFAULT_CRS = "EPSG:4326"
        const val PARAM = "feature"
    }

    override val type: ArgumentType<FeatureOption> = ArgumentType()
    private val polygonType = ArgumentType<Polygon>()

    override fun generateMissingException(): Exception {
        return RequiredParameterMissingException(PARAM)
    }

    override suspend fun parse(request: Request): FeatureOption? {
        return request.body(PARAM, polygonType)?.let {
            val crs = request.parameter("featureCrs").lastOrNull()
            FeatureOption(it, crs ?: DEFAULT_CRS)
        }
    }

    override fun box(option: FeatureOption): Nothing {
        throw IllegalParameterException("$PARAM is not supported.")
    }
}
