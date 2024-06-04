package io.github.tronto20.titiler.domain

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Percentile(
    val value: Int,
) {
    init {
        require(value >= 0) {
            "Percentile must be >= 0"
        }
        require(value <= 100) {
            "Percentile must be <= 100"
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}
