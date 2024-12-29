package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.BuiltInType
import tree.maple.kasima.spellEngine.compiler.Type


object OpAdd : Operator() {
    override val type: Type.Function = Type.Function(listOf(BuiltInType.NUMBER, BuiltInType.NUMBER, BuiltInType.NUMBER))

    @JvmStatic
    fun apply(a: Double, b: Double): Double = a + b
}