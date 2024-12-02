package dev.tronto.titiler.core.outgoing.port

interface CRSFactory {
    fun create(crsString: String): CRS
}
