package dev.tronto.titiler.spring.application.image

import dev.tronto.titiler.image.domain.Image
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.codec.Encoder
import org.springframework.http.MediaType
import org.springframework.http.ReactiveHttpOutputMessage
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Mono

class ImageHttpMessageWriter(
    private val imageEncoder: Encoder<Image>,
) : HttpMessageWriter<Image> {
    override fun getWritableMediaTypes(): MutableList<MediaType> {
        return imageEncoder.getEncodableMimeTypes().map { MediaType(it) }.toMutableList()
    }

    override fun canWrite(elementType: ResolvableType, mediaType: MediaType?): Boolean {
        return imageEncoder.canEncode(elementType, mediaType)
    }

    override fun write(
        inputStream: Publisher<out Image>,
        elementType: ResolvableType,
        mediaType: MediaType?,
        message: ReactiveHttpOutputMessage,
        hints: MutableMap<String, Any>,
    ): Mono<Void> {
        return if (inputStream is Mono<out Image>) {
            inputStream.flatMap {
                val mediaType = MediaType.parseMediaType(it.format.contentType)
                message.headers.contentType = mediaType
                message.writeWith(
                    imageEncoder.encode(inputStream, message.bufferFactory(), elementType, mediaType, hints)
                )
            }
        } else {
            throw UnsupportedOperationException()
        }
    }

    override fun write(
        inputStream: Publisher<out Image>,
        actualType: ResolvableType,
        elementType: ResolvableType,
        mediaType: MediaType?,
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        hints: MutableMap<String, Any>,
    ): Mono<Void> {
        return super.write(inputStream, actualType, elementType, mediaType, request, response, hints)
    }
}
