package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.Type
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import kotlin.reflect.KClass


abstract class Operator {

    abstract val type: Type.Function

    open val handle: MethodHandle by lazy {
        val lookup = MethodHandles.lookup()
        val returnType = type.signature.last()
        val methodType =
            MethodType.methodType(getRawType(returnType), type.signature.dropLast(1).map { getRawType(it) })
        lookup.findStatic(this::class.java, "apply", methodType)
    }

    companion object {
        private fun getRawType(type: Type): Class<*> =
            when (type) {
                is Type.Function -> MethodHandle::class
                is Type.Named -> type.rawType
                is Type.Var -> Any::class
                is Type.Generic -> Any::class
            }.java
    }
}