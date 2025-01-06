package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.Type
import tree.maple.kasima.spellEngine.compiler.bind
import tree.maple.kasima.spellEngine.compiler.flatten
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
    fun <T: Any, U: Any> apply(f: MethodHandle, g: MethodHandle, x: T): U = f.bind(g.bind(x).flatten()).flatten() as U

}

// (b -> c) -> (a -> b) -> a -> c
// (a -> a) -> a -> a -> a