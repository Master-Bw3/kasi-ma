package tree.maple.kasima.spellEngine.types

import kotlin.reflect.KClass

class BooleanValue(val value: Boolean) : Value() {
    override val type: TypeConstructor<*> = BooleanTypeConstructor

    override fun toString(): String {
        return "Boolean(${value})"
    }
}

object BooleanTypeConstructor : TypeConstructor<BooleanValue>() {
    override fun construct(value: Any): BooleanValue {
        if (value is Boolean) {
            return BooleanValue(value)
        } else {
            throw IllegalArgumentException()
        }
    }

    override val rawType: KClass<*>
        get() = Boolean::class

    override val string: String
        get() = "Boolean"

}
