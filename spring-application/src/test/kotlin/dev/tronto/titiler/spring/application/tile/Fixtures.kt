package dev.tronto.titiler.spring.application.tile

import dev.tronto.titiler.spring.application.core.infoFields
import dev.tronto.titiler.spring.application.core.testInfo
import dev.tronto.titiler.tile.domain.TileInfo
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.SubsectionDescriptor
import org.springframework.restdocs.snippet.Attributes

val testTileInfo = TileInfo(10, 15, testInfo)
val tileInfoFields = infoFields.map {
    it.copy(path = "info.${it.path}")
} + listOf(
    PayloadDocumentation.fieldWithPath("minZoom").description("Minimum zoom of dataset.").type(JsonFieldType.NUMBER),
    PayloadDocumentation.fieldWithPath("maxZoom").description("Maximum zoom of dataset.").type(JsonFieldType.NUMBER)
)

fun FieldDescriptor.copy(path: String? = null): FieldDescriptor {
    val descriptor = if (this is SubsectionDescriptor) {
        PayloadDocumentation.subsectionWithPath(path ?: this.path)
    } else {
        PayloadDocumentation.fieldWithPath(path ?: this.path)
    }
    descriptor.description(this.description)
    descriptor.type(this.type)
    if (this.isOptional) {
        descriptor.optional()
    }
    if (this.isIgnored) {
        descriptor.ignored()
    }
    descriptor.attributes(*this.attributes.map { Attributes.Attribute(it.key, it.value) }.toTypedArray())
    return descriptor
}
