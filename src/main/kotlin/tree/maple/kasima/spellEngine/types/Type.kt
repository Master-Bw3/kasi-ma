package tree.maple.kasima.spellEngine.types

import kotlin.reflect.KClass

abstract class Type<T : Value> {

    abstract fun construct(value: Any): T

    abstract val rawType: KClass<*>

    abstract val string: String
}