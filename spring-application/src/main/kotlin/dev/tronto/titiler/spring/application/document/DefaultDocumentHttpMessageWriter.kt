package dev.tronto.titiler.spring.application.document

import dev.tronto.titiler.document.domain.Document
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.http.MediaType
import org.springframework.http.ReactiveHttpOutputMessage
import org.springframework.http.codec.HttpMessageWriter
import reactor.core.publisher.Mono

class DefaultDocumentHttpMessageWriter : HttpMessageWriter<Document> {
    companion object {
        @JvmStatic
        private val documentType = ResolvableType.forClass(Document::class.java)
    }

    override fun getWritableMediaTypes(): MutableList<MediaType> {
        return emptyList<MediaType>().toMutableList()
    }

    override fun canWrite(elementType: ResolvableType, mediaType: MediaType?): Boolean {
        return mediaType == null && documentType.isAssignableFrom(elementType)
    }

    override fun write(
        inputStream: Publisher<out Document>,
        elementType: ResolvableType,
        mediaType: MediaType?,
        message: ReactiveHttpOutputMessage,
        hints: MutableMap<String, Any>,
    ): Mono<Void> {
        return if (inputStream is Mono<out Document>) {
            inputStream.flatMap {
                val mediaType = MediaType.parseMediaType(it.format.contentType)
                message.headers.contentType = mediaType
                message.writeWith(
                    Mono.just(message.bufferFactory().wrap(it.contents.toByteArray(Charsets.UTF_8)))
                )
            }
        } else {
            throw UnsupportedOperationException()
        }
    }
}
