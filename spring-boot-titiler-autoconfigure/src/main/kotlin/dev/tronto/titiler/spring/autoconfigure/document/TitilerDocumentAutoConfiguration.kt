package dev.tronto.titiler.spring.autoconfigure.document

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.thymeleaf.TemplateEngine

@AutoConfiguration
class TitilerDocumentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun templateEngine() = TemplateEngine()

    @Bean
    fun defaultDocumentCodecCustomizer() = CodecCustomizer {
        it.customCodecs().register(DefaultDocumentHttpMessageWriter())
    }
}
