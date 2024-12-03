package dev.tronto.titiler.image.outgoing.port

import dev.tronto.titiler.image.domain.ImageFormat

/**
 *  [ImageData] 중 자신이 가능한 객체를 렌더링 해주는 인터페이스
 */
interface ImageDataRenderer {
    fun supports(imageData: ImageData, format: ImageFormat): Boolean

    fun render(imageData: ImageData, format: ImageFormat): ByteArray
}
