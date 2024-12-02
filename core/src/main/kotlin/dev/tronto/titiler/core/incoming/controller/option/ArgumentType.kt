package dev.tronto.titiler.core.incoming.controller.option

import org.springframework.core.ParameterizedTypeReference
import java.lang.reflect.Type
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class ArgumentType<T : Any>(
    val kType: KType,
    val javaType: Type,
) {

    companion object {
        inline operator fun <reified T : Any> invoke(): ArgumentType<T> {
            // fixme :: KType 만 했을 때에는 Java 에서 선언한 Class 에서 에러가 발생함.
            val typeReference: ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}
            return ArgumentType(
                typeOf<T>(),
                typeReference.type
            )
        }
    }
}
