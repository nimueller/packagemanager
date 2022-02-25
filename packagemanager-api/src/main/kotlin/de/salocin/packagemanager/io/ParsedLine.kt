package de.salocin.packagemanager.io

import kotlin.reflect.KClass
import kotlin.reflect.typeOf

@JvmInline
value class ParsedLine(val value: List<String>) {

    fun <T : Any> buildValueClassInstance(targetClass: KClass<T>): T {
        for (constructor in targetClass.constructors) {
            if (constructor.parameters.count() == value.size &&
                constructor.parameters.all { it.type == typeOf<String>() }
            ) {
                return constructor.call(*value.toTypedArray())
            }
        }

        throw IllegalStateException("No constructor found in class $targetClass for ${value.size} String values")
    }

    operator fun get(index: Int) = value[index]

    operator fun contains(element: String) = element in value
}
