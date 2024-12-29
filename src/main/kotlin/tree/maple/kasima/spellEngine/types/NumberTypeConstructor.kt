package tree.maple.kasima.spellEngine.types

import kotlin.reflect.KClass

object NumberTypeConstructor : TypeConstructor<NumberValue>() {
    override fun construct(value: Any): NumberValue {
        if (value is Double) {
            return NumberValue(value)
        } else {
            throw IllegalArgumentException()
        }
    }

    override val rawType: KClass<*>
        get() = Double::class

    override val string: String
        get() = "number"

}

class NumberValue : Value {

    var value: Double = 0.0
        private set

    override val type: TypeConstructor<*> = NumberTypeConstructor

    constructor(value: Double) {
        this.value = value
    }


    override fun toString(): String {
        return "Number(${value})"
    }
}
