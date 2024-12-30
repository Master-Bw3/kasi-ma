package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.BuiltInType
import tree.maple.kasima.spellEngine.compiler.Type


object OpAdd : SimpleOperator() {
    override val type: Type.Function = Type.Function(listOf(BuiltInType.number, BuiltInType.number, BuiltInType.number))

    @JvmStatic
    fun apply(a: Double, b: Double): Double = a + b
}