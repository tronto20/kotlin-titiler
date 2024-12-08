package dev.tronto.titiler.image.domain

interface Image {
    val data: ByteArray
    val format: ImageFormat
}
