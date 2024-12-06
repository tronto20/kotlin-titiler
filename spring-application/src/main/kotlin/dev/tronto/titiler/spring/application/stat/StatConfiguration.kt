package dev.tronto.titiler.spring.application.stat

import dev.tronto.titiler.image.incoming.usecase.ImagePreviewUseCase
import dev.tronto.titiler.stat.service.StatisticsService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(StatPathProperties::class)
class StatConfiguration {
    @Bean
    fun statisticsService(imagePreviewUseCase: ImagePreviewUseCase) = StatisticsService(imagePreviewUseCase)
}
