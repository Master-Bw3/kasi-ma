package tree.maple.kasima.spellEngine.operators.composition

import tree.maple.kasima.spellEngine.compiler.Type
import tree.maple.kasima.spellEngine.compiler.bind
import tree.maple.kasima.spellEngine.compiler.flatten
import tree.maple.kasima.spellEngine.operators.Operator
import java.lang.invoke.MethodHandle

object OpFork : Operator() {
    override val type: Type.Function = Type.Function(
        listOf(
            Type.Function(listOf(Type.Generic(1u), Type.Generic(2u))),
            Type.Function(listOf(Type.Generic(1u), Type.Generic(3u))),
            Type.Function(listOf(Type.Generic(2u), Type.Generic(3u), Type.Generic(4u))),
            Type.Generic(1u),
            Type.Generic(4u),
            )
    )

    @JvmStatic
    fun <T1, T4> apply(f: MethodHandle, g: MethodHandle, collector: MethodHandle, x: T1): T4 {
        return collector
            .bind(f.bind(x).flatten())
            .bind(g.bind(x).flatten())
            .flatten() as T4
    }
}