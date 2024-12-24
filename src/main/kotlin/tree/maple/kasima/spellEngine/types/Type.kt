package tree.maple.kasima.spellEngine.types

import tree.maple.kasima.api.registry.ValueEndecRegistry
import kotlin.reflect.KClass

abstract class Type<T : Value> {

    abstract fun of(value: Any): T

    abstract val rawType: KClass<*>

    abstract val string: String
}