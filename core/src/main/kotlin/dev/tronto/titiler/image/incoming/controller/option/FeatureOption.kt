package dev.tronto.titiler.image.incoming.controller.option

import org.locationtech.jts.geom.Polygon

data class FeatureOption(
    val polygon: Polygon,
    val crsString: String,
) : ImageOption
