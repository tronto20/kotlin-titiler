package dev.tronto.titiler.core.domain

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Percentile(
    val value: Int,
) : Comparable<Percentile> {

    init {
        require(value >= 0) {
            "Percentile must be >= 0"
        }
        require(value <= 100) {
            "Percentile must be <= 100"
        }
    }

    override fun compareTo(other: Percentile): Int {
        return value.compareTo(other.value)
    }

    override fun toString(): String {
        return value.toString()
    }
}
