package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.BuiltInType
import tree.maple.kasima.spellEngine.compiler.Type

object OpListPlus : Operator() {
    override val type: Type.Function = Type.Function(listOf(BuiltInType.list(Type.Generic(1u)), Type.Generic(1u), BuiltInType.list(Type.Generic(1u))))

    @JvmStatic
    fun <T> apply(a: List<T>, b: T): List<T> = a.plus(b)
}
