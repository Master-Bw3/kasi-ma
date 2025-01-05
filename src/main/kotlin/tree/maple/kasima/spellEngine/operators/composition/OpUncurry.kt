package tree.maple.kasima.spellEngine.operators.composition

import tree.maple.kasima.spellEngine.compiler.BuiltInType
import tree.maple.kasima.spellEngine.compiler.Type
import tree.maple.kasima.spellEngine.operators.Operator
import java.lang.invoke.MethodHandle

object OpUncurry : Operator() {
    override val type: Type.Function = Type.Function(
        listOf(
            Type.Function(listOf(Type.Generic(1u), Type.Generic(2u), Type.Generic(3u))),
            BuiltInType.tuple(listOf(Type.Generic(1u), Type.Generic(2u))),
            Type.Generic(3u)
        )
    )

    @JvmStatic
    fun <T> apply(f: MethodHandle, x: List<Any>): T = f(x.first(), x.last()) as T
}