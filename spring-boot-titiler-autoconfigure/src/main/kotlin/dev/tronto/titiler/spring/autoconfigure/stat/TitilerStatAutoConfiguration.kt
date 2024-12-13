package dev.tronto.titiler.spring.autoconfigure.stat

import dev.tronto.titiler.image.incoming.usecase.ImagePreviewUseCase
import dev.tronto.titiler.spring.autoconfigure.image.TitilerImageAutoConfiguration
import dev.tronto.titiler.spring.autoconfigure.utils.sortedByOrdered
import dev.tronto.titiler.stat.incoming.usecase.StatisticsUseCase
import dev.tronto.titiler.stat.outgoing.port.spi.ImageDataStatistics
import dev.tronto.titiler.stat.service.StatisticsAutoRescale
import dev.tronto.titiler.stat.service.StatisticsService
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.annotation.Order
import java.util.function.Supplier

@ComponentScan
@AutoConfiguration(after = [TitilerImageAutoConfiguration::class])
@EnableConfigurationProperties(TitilerStatPathProperties::class)
class TitilerStatAutoConfiguration(applicationContext: GenericApplicationContext) {
    init {
        ImageDataStatistics.services.forEach {
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
    @ConditionalOnBean(ImagePreviewUseCase::class)
    fun statisticsService(
        imagePreviewUseCase: ImagePreviewUseCase,
        imageDataStatistics: ObjectProvider<ImageDataStatistics>,
    ) = StatisticsService(imagePreviewUseCase, imageDataStatistics.sortedByOrdered())

    @Bean
    @Order(-1)
    @ConditionalOnBean(StatisticsUseCase::class)
    fun statisticsAutoRescale(statisticsUseCase: StatisticsUseCase) = StatisticsAutoRescale(statisticsUseCase)
}
