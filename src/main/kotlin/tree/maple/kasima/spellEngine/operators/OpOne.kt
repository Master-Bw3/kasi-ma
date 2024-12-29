package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.types.NumberTypeConstructor
import tree.maple.kasima.spellEngine.types.NumberValue
import tree.maple.kasima.spellEngine.types.SpellFunction
import tree.maple.kasima.spellEngine.types.TypeConstructor

object OpOne : SpellFunction() {
    override val signature: List<TypeConstructor<*>> = listOf(NumberTypeConstructor)

    @JvmStatic
    fun apply(): Double = 1.0
}