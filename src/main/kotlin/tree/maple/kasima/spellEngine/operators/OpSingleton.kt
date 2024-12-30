package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.BuiltInType
import tree.maple.kasima.spellEngine.compiler.Type

object OpSingleton : Operator() {
    override val type: Type.Function = Type.Function(listOf(Type.Generic(1u), BuiltInType.list(Type.Generic(1u))))

    @JvmStatic
    fun <T> apply(a: T): List<T> = listOf(a)
}
