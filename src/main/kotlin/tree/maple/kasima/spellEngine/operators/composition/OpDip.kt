package tree.maple.kasima.spellEngine.operators.composition

import tree.maple.kasima.spellEngine.compiler.Type
import tree.maple.kasima.spellEngine.operators.Operator
import java.lang.invoke.MethodHandle

object OpDip : Operator() {
    override val type: Type.Function = Type.Function(
        listOf(
            Type.Function(listOf(Type.Generic(1u), Type.Generic(3u))),
            Type.Function(listOf(Type.Generic(1u), Type.Generic(2u), Type.Generic(4u))),
            Type.Generic(1u),
            Type.Generic(2u),
            Type.Generic(4u)
        )
    )

    @JvmStatic
    fun <T1, T2, T4> apply(f: MethodHandle, collector: MethodHandle, a: T1, b: T2): T4 {
        return collector(a, f(b)) as T4
    }
}