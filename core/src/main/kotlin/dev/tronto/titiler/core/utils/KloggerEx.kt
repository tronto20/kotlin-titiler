package dev.tronto.titiler.core.utils

import io.github.oshai.kotlinlogging.KLogger
import kotlin.time.measureTime

inline fun <T> KLogger.logTrace(name: String, block: () -> T): T {
    this.trace { "start $name" }
    var result: Result<T>? = null
    val time = measureTime {
        result = kotlin.runCatching {
            block()
        }
    }
    this.trace { "end $name - $time" }
    return result!!.getOrThrow()
}
