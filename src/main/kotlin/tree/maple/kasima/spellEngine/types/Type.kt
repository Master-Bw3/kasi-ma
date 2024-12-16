package tree.maple.kasima.spellEngine.types

import kotlin.reflect.KClass

abstract class Type<T: Value> {

    abstract fun of(value: Any) : T

    abstract val rawType : KClass<*>

    companion object {
        fun of(value: Any) : Type<*> {
            TODO()
        }
    }
}