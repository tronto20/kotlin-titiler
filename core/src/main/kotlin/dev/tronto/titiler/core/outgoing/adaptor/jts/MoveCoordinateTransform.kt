package dev.tronto.titiler.core.outgoing.adaptor.jts

import dev.tronto.titiler.core.outgoing.port.CoordinateTransform
import org.locationtech.jts.geom.util.AffineTransformation

@Suppress("FunctionName")
fun MoveCoordinateTransform(x: Double, y: Double): CoordinateTransform =
    AffineCoordinateTransform(AffineTransformation.translationInstance(x, y))
