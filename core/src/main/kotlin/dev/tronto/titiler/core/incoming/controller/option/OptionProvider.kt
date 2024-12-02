package dev.tronto.titiler.core.incoming.controller.option

class OptionProvider<O : Option>(
    @PublishedApi
    internal val options: List<O>,
    @PublishedApi
    internal val parsers: Iterable<OptionParser<*>>,
) {
    inline fun <reified T : Option> filter(): OptionProvider<T> {
        return OptionProvider<T>(options.filterIsInstance<T>(), parsers)
    }

    inline fun <reified T : O> list(): List<T> = options.filterIsInstance<T>()

    inline fun <reified T : O> getOrNull(): T? {
        return options.filterIsInstance<T>().lastOrNull()
    }

    inline fun <reified T : O> get(): T {
        return getOrNull<T>() ?: run {
            val resultType = ArgumentType<T>()
            val exception = parsers.find { it.type == resultType }?.generateMissingException()
                ?: IllegalArgumentException("Required option ${T::class.simpleName} not found")
            throw exception
        }
    }

    operator fun plus(option: O): OptionProvider<O> {
        return OptionProvider(options + option, parsers)
    }

    operator fun plus(options: Iterable<O>): OptionProvider<O> {
        return OptionProvider(this.options + options, parsers)
    }
}
