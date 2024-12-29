package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.types.NumberTypeConstructor
import tree.maple.kasima.spellEngine.types.NumberValue
import tree.maple.kasima.spellEngine.types.SpellFunction
import tree.maple.kasima.spellEngine.types.TypeConstructor

object OpAdd : SpellFunction() {
    override val signature: List<TypeConstructor<*>> = listOf(NumberTypeConstructor, NumberTypeConstructor, NumberTypeConstructor)

    @JvmStatic
    fun apply(a: Double, b: Double): Double = a + b
}