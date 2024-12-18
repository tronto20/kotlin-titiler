package dev.tronto.titiler.spring.autoconfigure.swagger

import dev.tronto.titiler.spring.autoconfigure.webflux.TitilerWebAutoConfiguration
import io.swagger.v3.parser.core.models.SwaggerParseResult
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.ComponentScan

@AutoConfiguration(after = [TitilerWebAutoConfiguration::class])
@ConditionalOnClass(SwaggerParseResult::class)
@ComponentScan
class TitilerSwaggerAutoConfiguration
