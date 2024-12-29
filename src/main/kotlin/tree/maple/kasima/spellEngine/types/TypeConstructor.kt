package tree.maple.kasima.spellEngine.types

import kotlin.reflect.KClass

abstract class TypeConstructor<T : Value> : Type() {

    abstract fun construct(value: Any): T

    open val generics: List<Type> = listOf()

    abstract val rawType: KClass<*>

    abstract val string: String

    override fun toString(): String = this.string
}