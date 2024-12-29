package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.BuiltInType
import tree.maple.kasima.spellEngine.compiler.Type

object OpOne : Operator() {
    override val type = Type.Function(listOf(BuiltInType.NUMBER))

    @JvmStatic
    fun apply(): Double = 1.0
}