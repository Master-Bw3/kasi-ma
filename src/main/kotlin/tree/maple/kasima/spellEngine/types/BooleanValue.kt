package tree.maple.kasima.spellEngine.types

import kotlin.reflect.KClass

class BooleanValue(val value: Boolean) : Value() {

    companion object {
        val TYPE = object : Type<BooleanValue>() {
            override fun of(value: Any): BooleanValue  {
                if (value is Boolean) {
                    return BooleanValue(value)
                } else {
                    throw IllegalArgumentException()
                }
            }

            override val rawType: KClass<*>
                get() = Boolean::class

        }
    }

    override fun toString(): String {
        return "Boolean(${value})"
    }
}