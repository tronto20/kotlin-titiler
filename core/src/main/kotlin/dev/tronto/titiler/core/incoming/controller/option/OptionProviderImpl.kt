package dev.tronto.titiler.core.incoming.controller.option

class OptionProviderImpl<O : Option>(
    private val request: Request,
    override val argumentType: ArgumentType<O>,
    private val parserMap: Map<ArgumentType<out O>, List<OptionParser<out O>>>,
    private val parseCache: MutableMap<ArgumentType<out O>, O?> = mutableMapOf(),
) : OptionProvider<O> {
    override fun <T : O> filter(argumentType: ArgumentType<T>): OptionProvider<T> {
        return OptionProviderImpl<T>(
            request,
            argumentType,
            parserMap
                .filter { it.key.isSubtypeOf(argumentType) }
                .mapKeys { it.key as ArgumentType<out T> }
                .mapValues { it.value as List<OptionParser<out T>> },
            mutableMapOf<ArgumentType<out T>, T?>().apply {
                putAll(
                    parseCache.filter { it.key.isSubtypeOf(argumentType) }
                        .mapKeys { it.key as ArgumentType<out T> }
                        .mapValues { it.value as T? }
                )
            }
        )
    }

    override fun <T : O> filterNot(argumentType: ArgumentType<T>): OptionProvider<O> {
        return OptionProviderImpl<O>(
            request,
            this.argumentType,
            parserMap.filterNot { it.key.isSubtypeOf(argumentType) },
            parseCache.filterNot { it.key.isSubtypeOf(argumentType) }.toMutableMap()
        )
    }

    override fun <T : O> getAll(argumentType: ArgumentType<T>): List<T> {
        val cachedType = mutableSetOf<ArgumentType<out T>>()
        val cachedValues = parseCache.entries
            .filter { it.key.isSubtypeOf(argumentType) }
            .onEach { cachedType.add(it.key as ArgumentType<out T>) }
            .mapNotNull { it.value as T? }
        return cachedValues + parserMap
            .filterNot { it.key in cachedType }
            .filter { it.key.isSubtypeOf(argumentType) }
            .mapNotNull { getOrNull(it.key as ArgumentType<out T>) }
    }

    override fun <T : O> get(argumentType: ArgumentType<T>): T {
        getOrNull(argumentType)?.let { return it }
        val parser = parserMap[argumentType]?.firstOrNull()
            ?: throw IllegalStateException("Parameter parser for $argumentType not defined.")
        throw parser.generateMissingException()
    }

    override fun <T : O> getOrNull(argumentType: ArgumentType<T>): T? {
        if (parseCache.containsKey(argumentType)) {
            return parseCache[argumentType] as T?
        }
        return parserMap[argumentType]?.asReversed()?.firstNotNullOfOrNull {
            it.parse(request) as T?
        }.also {
            parseCache[argumentType] = it
        }
    }

    override fun <T : O> plus(option: T, argumentType: ArgumentType<T>): OptionProvider<O> {
        return OptionProviderImpl<O>(
            request,
            this.argumentType,
            parserMap,
            mutableMapOf<ArgumentType<out O>, O?>().apply {
                putAll(parseCache)
                put(argumentType, option)
            }
        )
    }

    override fun plus(other: OptionProvider<O>): OptionProvider<O> {
        return CombinedOptionProvider(listOf(this, other), argumentType)
    }

    override fun <T : O> boxAll(argumentType: ArgumentType<T>): Map<String, List<String>> {
        val targetParsers = parserMap.filter {
            it.key.isSubtypeOf(argumentType)
        }.mapKeys {
            it.key as ArgumentType<T>
        }.mapValues {
            it.value as List<OptionParser<out T>>
        }

        val resultMap = mutableMapOf<String, List<String>>()
        targetParsers.entries.forEach { (type, parsers) ->
            val value = getOrNull(type) ?: return@forEach
            val boxParser = parsers.firstOrNull()
                ?: throw IllegalStateException("Parameter parser for $argumentType not defined.")
            val box = (boxParser as OptionParser<T>).box(value)
            resultMap.putAll(box)
        }
        return resultMap
    }
}
