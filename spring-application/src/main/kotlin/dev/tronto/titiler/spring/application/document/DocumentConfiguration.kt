package dev.tronto.titiler.spring.application.document

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.TemplateEngine

@Configuration
class DocumentConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun templateEngine() = TemplateEngine()

    @Bean
    fun defaultDocumentCodecCustomizer() = CodecCustomizer {
        it.customCodecs().register(DefaultDocumentHttpMessageWriter())
    }
}
