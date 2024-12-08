package dev.tronto.titiler.spring.application.core.hints

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import java.nio.ByteBuffer
import java.util.*

/**
 *  Gdal 에서 직접 생성하는 Java Class 들은 인스턴스를 생성하기 위해 Reflection 정보가 필요하기에 등록 필요.
 */
class GdalJNIRuntimeHints : RuntimeHintsRegistrar {

    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        val jni = hints.jni()
        val targets = listOf(
            Hashtable::class.java,
            Vector::class.java,
            java.lang.Double::class.java,
            ByteBuffer::class.java
        )
        targets.forEach {
            jni.registerType(
                it,
                MemberCategory.DECLARED_FIELDS,
                MemberCategory.DECLARED_CLASSES,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                MemberCategory.INVOKE_DECLARED_METHODS
            )
        }
    }
}
