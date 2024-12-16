package tree.maple.kasima.spellEngine.runes

import tree.maple.kasima.spellEngine.types.Type
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

abstract class Rune {

    abstract val arguments : List<Type<*>>

    abstract val returnType : Type<*>

    open val handle: MethodHandle by lazy {
        val lookup = MethodHandles.lookup()
        val methodType = MethodType.methodType(returnType.rawType.java, arguments.map { it.rawType.java })
        lookup.findStatic(this::class.java, "apply", methodType)
    }


}