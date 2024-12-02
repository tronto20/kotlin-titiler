package dev.tronto.titiler.core.outgoing.port

interface CRS {
    val name: String
    val wkt: String
    val proj4: String
    val unit: String
    val semiMajor: Double
    val semiMinor: Double
    val invertAxis: Boolean
}
