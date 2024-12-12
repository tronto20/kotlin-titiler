package dev.tronto.titiler.core.incoming.controller.option

import dev.tronto.titiler.core.domain.Ordered
import java.util.*

interface OptionParser<T : Option> {
    val type: ArgumentType<T>
    fun generateMissingException(): Exception

    fun parse(request: Request): T?
    fun box(option: T): Map<String, List<String>>

    companion object {
        @JvmStatic
        val services by lazy {
            ServiceLoader.load(OptionParser::class.java, Thread.currentThread().contextClassLoader)
                .toList()
                .sortedBy {
                    when (it) {
                        is Ordered -> it.order
                        else -> 0
                    }
                }
        }
    }
}
