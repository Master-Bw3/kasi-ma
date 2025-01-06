package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.BuiltInType
import tree.maple.kasima.spellEngine.compiler.Type
import tree.maple.kasima.spellEngine.compiler.bind
import tree.maple.kasima.spellEngine.compiler.flatten
import java.lang.invoke.MethodHandle

object OpListMap : Operator() {
    override val type: Type.Function = Type.Function(
        listOf(
            BuiltInType.list(Type.Generic(1u)),
            Type.Function(listOf(Type.Generic(1u), Type.Generic(2u))),
            BuiltInType.list(Type.Generic(2u))
        )
    )

    @JvmStatic
    fun <T, U> apply(list: List<T>, mapper: MethodHandle): List<U> = list.map { mapper.bind(it).flatten() as U }

}
