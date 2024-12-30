package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.BuiltInType
import tree.maple.kasima.spellEngine.compiler.Type

object OpOne : SimpleOperator() {
    override val type = Type.Function(listOf(BuiltInType.number))

    @JvmStatic
    fun apply(): Double = 1.0
}