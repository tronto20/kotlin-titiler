package dev.tronto.titiler.core.incoming.controller.option

interface Request {
    fun parameter(key: String): List<String>

    fun option(key: String): List<String>
}
