package dev.tronto.titiler.spring.application.image

import dev.tronto.titiler.image.outgoing.port.ImageDataAutoRescale
import dev.tronto.titiler.image.outgoing.port.ImageRenderer
import dev.tronto.titiler.image.service.ImageRenderService
import dev.tronto.titiler.spring.application.core.sortedByOrdered
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import java.util.*

@Configuration
class ImageRenderConfiguration(
    applicationContext: GenericApplicationContext,
) {
    init {
        ServiceLoader.load(ImageDataAutoRescale::class.java, Thread.currentThread().contextClassLoader).forEach {
            applicationContext.defaultListableBeanFactory.registerBeanDefinition(
                it::class.qualifiedName ?: it.toString(),
                GenericBeanDefinition().apply {
                    setBeanClass(it::class.java)
                    setInstanceSupplier { it }
                }
            )
        }
        ServiceLoader.load(ImageRenderer::class.java, Thread.currentThread().contextClassLoader).forEach {
            applicationContext.defaultListableBeanFactory.registerBeanDefinition(
                it::class.qualifiedName ?: it.toString(),
                GenericBeanDefinition().apply {
                    setBeanClass(it::class.java)
                    setInstanceSupplier { it }
                }
            )
        }
    }

    @Bean
    fun imageRenderService(
        imageRenderers: ObjectProvider<ImageRenderer>,
        imageDataAutoRescales: List<ImageDataAutoRescale>,
    ) = ImageRenderService(
        imageRenderers.sortedByOrdered(),
        imageDataAutoRescales.sortedByOrdered()
    )

    @Bean
    fun defaultImageCodecCustomizer() = CodecCustomizer {
        it.customCodecs().register(DefaultImageHttpMessageWriter())
    }
}
