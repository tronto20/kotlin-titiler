package dev.tronto.titiler.core.incoming.controller.option

inline fun <reified T : O, O : Option> OptionProvider<O>.filter(): OptionProvider<T> = filter(ArgumentType())
inline fun <reified T : O, O : Option> OptionProvider<O>.filterNot(): OptionProvider<O> = filterNot(ArgumentType<T>())

inline fun <reified T : O, O : Option> OptionProvider<O>.getAll(): List<T> = getAll(ArgumentType())
inline fun <reified T : O, O : Option> OptionProvider<O>.get(): T = get(ArgumentType())
inline fun <reified T : O, O : Option> OptionProvider<O>.getOrNull(): T? = getOrNull(ArgumentType())

inline operator fun <reified T : O, O : Option> OptionProvider<O>.plus(option: T): OptionProvider<O> =
    plus(option, ArgumentType<T>())

inline fun <reified T : O, O : Option> OptionProvider<O>.boxAll(): Map<String, List<String>> = boxAll(ArgumentType<T>())
