package dev.tronto.titiler.core.outgoing.port

interface CRSTransform : CoordinateTransform {
    object Empty : CRSTransform, CoordinateTransform by CoordinateTransform.Empty
}
