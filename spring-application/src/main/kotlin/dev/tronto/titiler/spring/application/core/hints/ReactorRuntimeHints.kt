package dev.tronto.titiler.spring.application.core.hints

import org.springframework.aot.hint.ExecutableMode
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import reactor.core.Disposable

class ReactorRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerMethod(Disposable::class.java.getMethod("dispose"), ExecutableMode.INVOKE)
    }
}
