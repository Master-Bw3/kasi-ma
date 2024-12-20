package tree.maple.kasima.spellEngine.types

import tree.maple.kasima.api.registry.TypeRegistry

abstract class Value {

    companion object {
        fun fromRawValue(value: Any) : Value {
            val entries = TypeRegistry.REGISTRY.entrySet.map { it.value }

            entries.forEach {
                val rawType = it.rawType
                if (value::class == rawType) {
                    return it.of(value)
                }
            }

            throw IllegalArgumentException()
        }
    }
}