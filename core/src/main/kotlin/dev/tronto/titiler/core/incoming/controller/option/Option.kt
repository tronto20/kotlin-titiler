package dev.tronto.titiler.core.incoming.controller.option

interface Option {
    companion object {
        operator fun <T : Option> T.plus(other: T): Iterable<T> {
            return listOf(this, other)
        }

        inline fun <reified T : Option> Iterable<Option>.getSingleOrNull(): T? {
            return this.filterIsInstance<T>().lastOrNull()
        }
    }
}
