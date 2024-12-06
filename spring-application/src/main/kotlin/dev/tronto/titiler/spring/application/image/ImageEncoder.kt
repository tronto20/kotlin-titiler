package dev.tronto.titiler.spring.application.image

import dev.tronto.titiler.image.domain.Image
import dev.tronto.titiler.image.domain.ImageFormat
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.codec.Encoder
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.http.MediaType
import org.springframework.util.MimeType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ImageEncoder() : Encoder<Image> {
    override fun canEncode(elementType: ResolvableType, mimeType: MimeType?): Boolean {
        return elementType.equalsType(ResolvableType.forClass(Image::class.java)) &&
            (mimeType == null || mimeType.type.equals("image", true))
    }

    override fun encode(
        inputStream: Publisher<out Image>,
        bufferFactory: DataBufferFactory,
        elementType: ResolvableType,
        mimeType: MimeType?,
        hints: MutableMap<String, Any>?,
    ): Flux<DataBuffer> {
        return if (inputStream is Mono<out Image>) {
            inputStream.flatMapMany {
                Flux.just(bufferFactory.wrap(it.data))
            }
        } else {
            throw IllegalStateException("inputStream must be Mono<out Image>")
        }
    }

    override fun encodeValue(
        value: Image,
        bufferFactory: DataBufferFactory,
        valueType: ResolvableType,
        mimeType: MimeType?,
        hints: MutableMap<String, Any>?,
    ): DataBuffer {
        return bufferFactory.wrap(value.data)
    }

    override fun getEncodableMimeTypes(): MutableList<MimeType> {
        return ImageFormat.entries.map {
            MediaType.parseMediaType(it.contentType)
        }.toMutableList()
    }
}
