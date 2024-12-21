package tree.maple.kasima.spellEngine.runes

import tree.maple.kasima.spellEngine.types.BooleanValue
import tree.maple.kasima.spellEngine.types.NumberValue
import tree.maple.kasima.spellEngine.types.Type

object RuneAddBool : Rune() {
    override val arguments: List<Type<*>>
        get() = listOf(BooleanValue.TYPE, NumberValue.TYPE)

    override val returnType: Type<*>
        get() = NumberValue.TYPE

    @JvmStatic
    fun apply(a: Boolean, b: Double): Double = (if (a) 1 else 0) + b
}