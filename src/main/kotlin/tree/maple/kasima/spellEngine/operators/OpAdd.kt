package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.types.NumberValue
import tree.maple.kasima.spellEngine.types.SpellFunction
import tree.maple.kasima.spellEngine.types.Type

object OpAdd : SpellFunction() {
    override val arguments: List<Type<*>> = listOf(NumberValue.TYPE, NumberValue.TYPE)

    override val returnType: Type<*>
        get() = NumberValue.TYPE

    @JvmStatic
    fun apply(a: Double, b: Double): Double = a + b
}