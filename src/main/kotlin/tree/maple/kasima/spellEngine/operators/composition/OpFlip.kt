package tree.maple.kasima.spellEngine.operators.composition

import tree.maple.kasima.spellEngine.compiler.Type
import tree.maple.kasima.spellEngine.compiler.bind
import tree.maple.kasima.spellEngine.compiler.flatten
import tree.maple.kasima.spellEngine.operators.Operator
import java.lang.invoke.MethodHandle

object OpFlip : Operator() {
    override val type: Type.Function = Type.Function(
        listOf(
            Type.Function(listOf(Type.Generic(1u), Type.Generic(2u), Type.Generic(3u))),
            Type.Generic(1u),
            Type.Generic(2u),
            Type.Generic(3u)
        )
    )

    @JvmStatic
    fun <T1, T2, T3> apply(f: MethodHandle , a: T1, b: T2): T3 =
        f.bind(b).bind(a).flatten() as T3
}