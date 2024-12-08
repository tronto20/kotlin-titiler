package dev.tronto.titiler.document.template

import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.thymeleaf.templateresource.ITemplateResource
import java.io.Reader
import java.io.Serializable
import java.nio.channels.Channels

class ClasspathFileTemplateResource(
    private val resource: Resource,
    private val encoding: String?,
) : ITemplateResource, Serializable {
    companion object {
        private val resolver = PathMatchingResourcePatternResolver()
    }

    constructor(path: String, encoding: String?) : this(
        resolver.getResources("classpath*:$path").firstOrNull()
            ?: resolver.getResource("classpath:$path"),
        encoding
    )

    override fun getDescription(): String? {
        return resource.description
    }

    override fun getBaseName(): String? {
        return resource.filename ?: resource.filename
    }

    override fun exists(): Boolean {
        return resource.exists()
    }

    override fun reader(): Reader {
        return Channels.newReader(resource.readableChannel(), encoding ?: "UTF-8")
    }

    override fun relative(relativeLocation: String): ITemplateResource {
        return ClasspathFileTemplateResource(resource.createRelative(relativeLocation), encoding)
    }
}
