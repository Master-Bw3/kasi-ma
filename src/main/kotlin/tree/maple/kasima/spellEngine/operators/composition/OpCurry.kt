package tree.maple.kasima.spellEngine.operators.composition

import tree.maple.kasima.spellEngine.compiler.BuiltInType
import tree.maple.kasima.spellEngine.compiler.Type
import tree.maple.kasima.spellEngine.operators.Operator
import java.lang.invoke.MethodHandle

object OpCurry : Operator() {
    override val type: Type.Function = Type.Function(
        listOf(
            Type.Function(listOf(BuiltInType.tuple(listOf(Type.Generic(1u), Type.Generic(2u))), Type.Generic(3u))),
            Type.Generic(1u),
            Type.Generic(2u),
            Type.Generic(3u)
        )
    )

    @JvmStatic
    fun <T, U, V> apply(f: MethodHandle, a: T, b: U): V = f(listOf(a, b)) as V
}