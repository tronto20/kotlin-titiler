package dev.tronto.titiler.core.incoming.controller.option

class CombinedOptionProvider<O : Option>(
    private val list: List<OptionProvider<O>>,
    override val argumentType: ArgumentType<O>,
) : OptionProvider<O> {
    override fun <T : O> filter(argumentType: ArgumentType<T>): OptionProvider<T> {
        return CombinedOptionProvider(list.map { it.filter(argumentType) }, argumentType)
    }

    override fun <T : O> filterNot(argumentType: ArgumentType<T>): OptionProvider<O> {
        return CombinedOptionProvider(
            list.map { it.filterNot(argumentType) },
            this.argumentType
        )
    }

    override fun <T : O> getAll(argumentType: ArgumentType<T>): List<T> {
        return list.flatMap { it.getAll(argumentType) }
    }

    override fun <T : O> getOrNull(argumentType: ArgumentType<T>): T? {
        list.forEach {
            it.getOrNull(argumentType)?.let { return it }
        }
        return null
    }

    override fun <T : O> get(argumentType: ArgumentType<T>): T {
        getOrNull(argumentType)?.let { return it }
        list.forEach {
            try {
                it.get(argumentType)
            } catch (e: IllegalStateException) {
                // ignore
            }
        }
        throw IllegalStateException("Parameter parser for $argumentType not defined.")
    }

    override fun <T : O> plus(option: T, argumentType: ArgumentType<T>): OptionProvider<O> {
        return CombinedOptionProvider(
            list.map { it.plus(option, argumentType) },
            this.argumentType
        )
    }

    override fun <T : O> boxAll(argumentType: ArgumentType<T>): Map<String, List<String>> {
        val result = mutableMapOf<String, List<String>>()
        list.forEach {
            result.putAll(it.boxAll(argumentType))
        }
        return result
    }

    override fun plus(other: OptionProvider<O>): OptionProvider<O> {
        return CombinedOptionProvider(list + other, argumentType)
    }
}
