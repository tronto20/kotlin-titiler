package dev.tronto.titiler.spring.application.tile

import dev.tronto.titiler.spring.application.copy
import dev.tronto.titiler.spring.application.core.infoFields
import dev.tronto.titiler.spring.application.core.testInfo
import dev.tronto.titiler.tile.domain.TileInfo
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation

val testTileInfo = TileInfo(10, 15, testInfo)
val tileInfoFields = infoFields.map {
    it.copy(path = "info.${it.path}")
} + listOf(
    PayloadDocumentation.fieldWithPath("minZoom").description("Minimum zoom of dataset.").type(JsonFieldType.NUMBER),
    PayloadDocumentation.fieldWithPath("maxZoom").description("Maximum zoom of dataset.").type(JsonFieldType.NUMBER)
)

