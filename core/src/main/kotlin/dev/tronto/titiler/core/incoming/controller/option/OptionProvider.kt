package dev.tronto.titiler.core.incoming.controller.option

interface OptionProvider<O : Option> {
    companion object {
        inline fun <reified T : Option> empty(): OptionProvider<T> {
            return OptionProviderImpl(Request.Empty, ArgumentType<T>(), emptyMap())
        }
    }
    val argumentType: ArgumentType<O>

    fun <T : O> filter(argumentType: ArgumentType<T>): OptionProvider<T>
    fun <T : O> filterNot(argumentType: ArgumentType<T>): OptionProvider<O>
    fun <T : O> getAll(argumentType: ArgumentType<T>): List<T>

    fun <T : O> get(argumentType: ArgumentType<T>): T
    fun <T : O> getOrNull(argumentType: ArgumentType<T>): T?
    fun <T : O> contains(argumentType: ArgumentType<T>): Boolean = getOrNull(argumentType) != null

    fun <T : O> plus(option: T, argumentType: ArgumentType<T>): OptionProvider<O>

    fun <T : O> boxAll(argumentType: ArgumentType<T>): Map<String, List<String>>
}
