package dev.tronto.titiler.spring.application.web

import dev.tronto.titiler.core.exception.DataNotExistsException
import dev.tronto.titiler.core.exception.IllegalParameterException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ServerWebExchange

@Component
@Order(-1)
class WebExceptionHandler : DefaultErrorAttributes() {
    companion object {
        private const val HANDLING_ERROR_ATTRIBUTE = ".HANDLE_ERROR"

        @JvmStatic
        private val logger = KotlinLogging.logger {}
    }

    private class ExceptionHandle(
        val statusCode: HttpStatus,
        val exception: Throwable,
    ) {
        val message: String?
            get() = exception.message
    }

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        val base = super.getErrorAttributes(request, options)
        val handle = request.exchange().getAttribute<ExceptionHandle>(HANDLING_ERROR_ATTRIBUTE)
        if (handle != null) {
            base["status"] = handle.statusCode.value()
            base["message"] = handle.message
            base["error"] = handle.statusCode.reasonPhrase
            logger.warn(handle.exception) { "handle error." }
        }
        return base
    }

    override fun storeErrorInformation(error: Throwable, exchange: ServerWebExchange) {
        super.storeErrorInformation(error, exchange)
        val exceptionHandle = when (error) {
            is IllegalParameterException -> ExceptionHandle(HttpStatus.BAD_REQUEST, error)
            is DataNotExistsException -> ExceptionHandle(HttpStatus.NOT_FOUND, error)
            else -> return
        }
        exchange.attributes.putIfAbsent(HANDLING_ERROR_ATTRIBUTE, exceptionHandle)
    }
}
