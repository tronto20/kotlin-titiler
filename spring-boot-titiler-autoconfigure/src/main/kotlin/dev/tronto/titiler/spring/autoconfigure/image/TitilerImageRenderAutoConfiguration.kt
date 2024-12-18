package dev.tronto.titiler.spring.autoconfigure.image

import dev.tronto.titiler.image.outgoing.port.ImageDataAutoRescale
import dev.tronto.titiler.image.outgoing.port.ImageRenderer
import dev.tronto.titiler.image.service.ImageRenderService
import dev.tronto.titiler.spring.autoconfigure.utils.sortedByOrdered
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.support.GenericApplicationContext
import java.util.function.Supplier

@AutoConfiguration
class TitilerImageRenderAutoConfiguration(
    applicationContext: GenericApplicationContext,
) {
    init {
        ImageDataAutoRescale.services.forEach {
            applicationContext.defaultListableBeanFactory.registerBeanDefinition(
                it::class.qualifiedName ?: it.toString(),
                GenericBeanDefinition().apply {
                    beanClass = it::class.java
                    instanceSupplier = Supplier { it }
                }
            )
        }
        ImageRenderer.services.forEach {
            applicationContext.defaultListableBeanFactory.registerBeanDefinition(
                it::class.qualifiedName ?: it.toString(),
                GenericBeanDefinition().apply {
                    beanClass = it::class.java
                    instanceSupplier = Supplier { it }
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
