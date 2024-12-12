package dev.tronto.titiler.core.incoming.controller.option

data class OptionDescription<T : Any>(
    val type: ArgumentType<T>,
    val name: String,
    val description: String? = null,
    val sample: T? = null,
    val enums: Iterable<T>? = null,
    val default: T? = null,
)

inline fun <reified T : Any> OptionDescription(
    name: String,
    description: String? = null,
    sample: T? = null,
    enums: Iterable<T>? = null,
    default: T? = null,
): OptionDescription<T> = OptionDescription(
    ArgumentType(),
    name,
    description,
    sample,
    enums,
    default
)
