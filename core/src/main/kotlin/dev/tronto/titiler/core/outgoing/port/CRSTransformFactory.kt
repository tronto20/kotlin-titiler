package dev.tronto.titiler.core.outgoing.port

interface CRSTransformFactory {
    fun create(source: CRS, destination: CRS): CRSTransform
}
