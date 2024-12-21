package tree.maple.kasima.spellEngine.types

import tree.maple.kasima.api.registry.TypeRegistry
import kotlin.reflect.KClass

abstract class Type<T: Value> {

    abstract fun of(value: Any) : T

    abstract val rawType : KClass<*>

    companion object {

        fun fromRawType(value: KClass<*>) : Type<*> {
            val entries = TypeRegistry.REGISTRY.entrySet.map { it.value }

            entries.forEach {
                val rawType = it.rawType
                if (value == rawType) {
                    return it
                }
            }

            throw IllegalArgumentException()
        }
    }
}