package tree.maple.kasima.spellEngine.types

import kotlin.reflect.KClass

class NumberValue : Value {

    var value: Double = 0.0
        private set

    constructor(value: Double) {
        this.value = value
    }

    companion object {
        val TYPE = object : Type<NumberValue>() {
            override fun of(value: Any): NumberValue  {
                if (value is Double) {
                    return NumberValue(value)
                } else {
                    throw IllegalArgumentException()
                }
            }

            override val rawType: KClass<*>
                get() = Double::class

        }
    }

    override fun toString(): String {
        return "Number(${value})"
    }
}