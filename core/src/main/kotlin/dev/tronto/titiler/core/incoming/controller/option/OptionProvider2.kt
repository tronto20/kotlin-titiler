package dev.tronto.titiler.core.incoming.controller.option

interface OptionProvider2<O : Option> {
    companion object {
        inline fun <reified T : O, O : Option> OptionProvider2<O>.filter(): OptionProvider2<T> = filter(ArgumentType())
        inline fun <reified T : O, O : Option> OptionProvider2<O>.filterNot(): OptionProvider2<O> =
            filterNot(ArgumentType<T>())

        suspend inline fun <reified T : O, O : Option> OptionProvider2<O>.getAll(): List<T> = getAll(ArgumentType())
        suspend inline fun <reified T : O, O : Option> OptionProvider2<O>.get(): T = get(ArgumentType())
        suspend inline fun <reified T : O, O : Option> OptionProvider2<O>.getOrNull(): T? = getOrNull(ArgumentType())

        inline operator fun <reified T : O, O : Option> OptionProvider2<O>.plus(option: T): OptionProvider2<O> =
            plus(option, ArgumentType<T>())
    }

    fun <T : O> filter(argumentType: ArgumentType<T>): OptionProvider2<T>
    fun <T : O> filterNot(argumentType: ArgumentType<T>): OptionProvider2<O>
    suspend fun <T : O> getAll(argumentType: ArgumentType<T>): List<T>

    suspend fun <T : O> get(argumentType: ArgumentType<T>): T
    suspend fun <T : O> getOrNull(argumentType: ArgumentType<T>): T?

    fun <T : O> plus(option: T, argumentType: ArgumentType<T>): OptionProvider2<O>
}
