package dev.tronto.titiler.spring.application

import com.epages.restdocs.apispec.ParameterDescriptorWithType
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.epages.restdocs.apispec.Schema
import com.epages.restdocs.apispec.SimpleType
import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import dev.tronto.titiler.core.incoming.controller.option.ArgumentType
import dev.tronto.titiler.core.incoming.controller.option.OptionDescription
import dev.tronto.titiler.core.incoming.controller.option.OptionParser
import dev.tronto.titiler.wmts.service.TemplateString
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.AbstractDescriptor
import org.springframework.restdocs.snippet.Attributes
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.reactive.server.WebTestClient

fun buildParameterDescriptors(desc: OptionDescription<*>): Pair<ParameterDescriptor, ParameterDescriptorWithType> {
    val parameterDescriptor = RequestDocumentation.parameterWithName(desc.name).apply {
        description(desc.description)
        attributes(Attributes.Attribute("sample", desc.sample.toString()))
        desc.default?.let { attributes(Attributes.Attribute("defaultValue", it.toString())) }
        desc.enums?.let { attributes(Attributes.Attribute("enumValues", it.map { it.toString() })) }
    }
    return parameterDescriptor to ParameterDescriptorWithType.fromParameterDescriptor(parameterDescriptor).apply {
        val type = when (desc.type) {
            ArgumentType<Int>(),
            ArgumentType<Short>(),
            ArgumentType<Long>(),
            -> SimpleType.INTEGER

            ArgumentType<Float>(),
            ArgumentType<Double>(),
            -> SimpleType.NUMBER

            ArgumentType<Boolean>(),
            -> SimpleType.BOOLEAN

            ArgumentType<String>() -> SimpleType.STRING
            else -> null
        }
        if (type == null) {
            // array
            when (desc.type) {
                ArgumentType<IntArray>(),
                ArgumentType<Array<Int>>(),
                -> attributes(Attributes.Attribute("itemsType", "integer"))

                ArgumentType<Array<String>>(),
                -> attributes(Attributes.Attribute("itemsType", "string"))
            }
        } else {
            this.type(type)
        }
    }
}

fun <T : AbstractDescriptor<T>> T.itemsType(type: JsonFieldType): T {
    return this.attributes(Attributes.Attribute("itemsType", type))
}

fun <T : AbstractDescriptor<T>> T.enumValues(values: List<Any>): T {
    return this.attributes(Attributes.Attribute("enumValues", values))
}

fun WebTestClient.testAndDocument(
    id: String,
    description: String,
    summary: String,
    responseSchema: String,
    pathPatterns: List<String>,
    parsers: List<OptionParser<*>>,
    vararg optionType: ArgumentType<*>,
    relaxedRequestFields: Boolean = false,
    requestFields: List<FieldDescriptor> = emptyList(),
    relaxedResponseFields: Boolean = false,
    responseFields: List<FieldDescriptor> = emptyList(),
) {
    val options = parsers.filter { parser ->
        optionType.any { parser.type.isSubtypeOf(it) }
    }.flatMap { it.descriptions() }
        .distinctBy { it.name.lowercase() }
        .associateBy { it.name.lowercase() }

    pathPatterns.forEachIndexed { index, pattern ->
        val pathTemplate = TemplateString(pattern)
        val pathOptions = pathTemplate.variables.map {
            options.getValue(it.lowercase())
        }
        val queryOptions = options.filterNot { it.key in pathTemplate.variables }.values
        val pathParameters = pathOptions.map { desc ->
            println(desc.name)
            buildParameterDescriptors(desc)
        }
        val queryParameters = queryOptions.map { desc ->
            buildParameterDescriptors(desc)
        }
        val details = ResourceSnippetParameters.builder()
            .description(description)
            .summary(summary)
            .pathParameters(pathParameters.map { it.second })
            .queryParameters(queryParameters.map { it.second })
            .responseSchema(Schema.schema(responseSchema))
        val snippets: MutableList<Snippet> = mutableListOf(
            RequestDocumentation.pathParameters(pathParameters.map { it.first }),
            RequestDocumentation.relaxedQueryParameters(queryParameters.map { it.first })
        )
        if (responseFields.isNotEmpty()) {
            if (relaxedResponseFields) {
                snippets.add(PayloadDocumentation.relaxedResponseFields(responseFields))
            } else {
                snippets.add(PayloadDocumentation.responseFields(responseFields))
            }
            details.responseFields(responseFields)
        }
        if (requestFields.isNotEmpty()) {
            if (relaxedRequestFields) {
                snippets.add(PayloadDocumentation.relaxedRequestFields(requestFields))
            } else {
                snippets.add(PayloadDocumentation.requestFields(requestFields))
            }
            details.requestFields(requestFields)
        }

        this.get()
            .uri(
                pattern + queryOptions.joinToString(prefix = "?", separator = "&") { "${it.name}={${it.name}}" },
                pathOptions.associate { it.name to it.sample } + queryOptions.associate { it.name to it.sample }
            )
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentationWrapper.document(
                    identifier = "$id-$index",
                    resourceDetails = details,
                    snippets = snippets.toTypedArray()
                )
            )
    }
}
