package tree.maple.kasima.spellEngine.operators

import tree.maple.kasima.spellEngine.compiler.Type
import tree.maple.kasima.spellEngine.compiler.getRawType
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType


abstract class Operator {

    abstract val type: Type.Function

    open fun getHandle(castedType: Type.Function): MethodHandle {
        val lookup = MethodHandles.lookup()
        val returnType = type.signature.last()
        val methodType = MethodType.methodType(getRawType(returnType), type.signature.dropLast(1).map { getRawType(it) })

        val castedReturnType = castedType.signature.last()
        val castedMethodType = MethodType.methodType(getRawType(castedReturnType), castedType.signature.dropLast(1).map { getRawType(it) })

        return lookup.findStatic(this::class.java, "apply", methodType).asType(castedMethodType)
    }

}