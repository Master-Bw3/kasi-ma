package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.types.NumberValue
import tree.maple.kasima.spellEngine.types.SpellFunction
import tree.maple.kasima.spellEngine.types.Type

object OpOne : SpellFunction() {
    override val signature: List<Type<*>> = listOf(NumberValue.TYPE)

    @JvmStatic
    fun apply(): Double = 1.0
}