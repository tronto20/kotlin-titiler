package dev.tronto.titiler.image.domain

enum class ImageFormat(val contentType: String) {
    PNG("image/png"),
    JPEG("image/jpeg"),
    ;

    companion object {
        private val entryMap = ImageFormat.entries.associateBy {
            it.name.lowercase()
        }

        operator fun invoke(name: String): ImageFormat {
            return entryMap.getOrElse(name.lowercase()) {
                throw IllegalArgumentException("Unknown image format: $name")
            }
        }
    }
}
