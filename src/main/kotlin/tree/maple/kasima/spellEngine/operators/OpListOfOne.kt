package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.Type

object OpListOfOne : Operator() {
    override val type: Type.Function = Type.Function(listOf(Type.Generic(1u), Type.Generic(1u)))

    @JvmStatic
    fun apply(a: Any): List<Any> = listOf(a)
}