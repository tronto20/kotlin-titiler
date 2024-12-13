package dev.tronto.titiler.spring.application.stat

import dev.tronto.titiler.image.incoming.usecase.ImagePreviewUseCase
import dev.tronto.titiler.spring.application.core.sortedByOrdered
import dev.tronto.titiler.stat.incoming.usecase.StatisticsUseCase
import dev.tronto.titiler.stat.outgoing.port.spi.ImageDataStatistics
import dev.tronto.titiler.stat.service.StatisticsAutoRescale
import dev.tronto.titiler.stat.service.StatisticsService
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.annotation.Order
import java.util.*

@Configuration
@EnableConfigurationProperties(StatPathProperties::class)
class StatConfiguration(
    applicationContext: GenericApplicationContext,
) {
    init {
        ImageDataStatistics.services.forEach {
            applicationContext.defaultListableBeanFactory.registerBeanDefinition(
                it::class.qualifiedName ?: it.toString(),
                GenericBeanDefinition().apply {
                    setBeanClass(it::class.java)
                }
            )
        }
    }

    @Bean
    fun statisticsService(
        imagePreviewUseCase: ImagePreviewUseCase,
        imageDataStatistics: ObjectProvider<ImageDataStatistics>,
    ) = StatisticsService(imagePreviewUseCase, imageDataStatistics.sortedByOrdered())

    @Bean
    @Order(-1)
    fun statisticsAutoRescale(statisticsUseCase: StatisticsUseCase) = StatisticsAutoRescale(statisticsUseCase)
}
