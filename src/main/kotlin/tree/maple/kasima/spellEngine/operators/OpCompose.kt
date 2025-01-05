package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.Type
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles

object OpCompose : Operator() {
    override val type: Type.Function = Type.Function(
        listOf(
            Type.Function(listOf(Type.Generic(2u), Type.Generic(3u))),
            Type.Function(listOf(Type.Generic(1u), Type.Generic(2u))),
            Type.Generic(1u),
            Type.Generic(3u)
        )
    )

    @JvmStatic
    fun <T, U> apply(f: MethodHandle, g: MethodHandle, x: T): U = f(g(x)) as U
}