package dev.tronto.titiler.core.incoming.controller.option

class OptionProviderImpl<O : Option>(
    private val request: Request,
    private val parserMap: Map<ArgumentType<*>, List<OptionParser<*>>>,
    private val parseCache: MutableMap<ArgumentType<*>, Option?> = mutableMapOf(),
) : OptionProvider2<O> {
    override fun <T : O> filter(argumentType: ArgumentType<T>): OptionProvider2<T> {
        return OptionProviderImpl<T>(
            request,
            parserMap.filter { it.key.isSubtypeOf(argumentType) },
            parseCache
        )
    }

    override fun <T : O> filterNot(argumentType: ArgumentType<T>): OptionProvider2<O> {
        return OptionProviderImpl<O>(
            request,
            parserMap.filterNot { it.key.isSubtypeOf(argumentType) },
            parseCache
        )
    }

    override suspend fun <T : O> getAll(argumentType: ArgumentType<T>): List<T> {
        val cachedOptions = parseCache.filter { it.key.isSubtypeOf(argumentType) }
        val fetched = parserMap.filter {
            it.key !in cachedOptions.keys && it.key.isSubtypeOf(argumentType)
        }.map { (type, parsers) ->
            type to parsers.asReversed().firstNotNullOfOrNull { it.parse(request) as T? }
        }.toMap()
        fetched.forEach {
            parseCache[it.key] = it.value
        }
        return cachedOptions.values.mapNotNull { it as T? } + fetched.values.filterNotNull()
    }

    override suspend fun <T : O> get(argumentType: ArgumentType<T>): T {
        // TODO
        getOrNull(argumentType)?.let { return it }
        val parser = parserMap[argumentType]?.lastOrNull()
            ?: throw IllegalStateException("Parameter parser not defined.")
        throw parser.generateMissingException()
    }

    override suspend fun <T : O> getOrNull(argumentType: ArgumentType<T>): T? {
        if (parseCache.containsKey(argumentType)) {
            return parseCache[argumentType] as T?
        }
        return parserMap[argumentType]?.asReversed()?.firstNotNullOfOrNull {
            it.parse(request) as T?
        }.also {
            parseCache[argumentType] = it
        }
    }

    override fun <T : O> plus(option: T, argumentType: ArgumentType<T>): OptionProvider2<O> {
        parseCache[argumentType] = option
        return this
    }
}
