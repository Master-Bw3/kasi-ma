package tree.maple.kasima.spellEngine.compiler

import java.lang.invoke.MethodHandle
import kotlin.reflect.KClass

sealed class Type {
    data class Named(val name: String, val rawType: KClass<*>, val genericArgs: List<Type>) : Type()

    data class Function(val signature: List<Type>) : Type()

    //type var that has not been inferred yet
    data class Var(val index: Int) : Type()

    //generic type that stands in place of any type
    data class Generic(val id: UInt) : Type()

}

object BuiltInType {
    val number = Type.Named("Number", Double::class, listOf())
    fun list(a: Type.Generic) = Type.Named("List", List::class, listOf(a))
}

fun getRawType(type: Type): Class<*> =
    when (type) {
        is Type.Function -> MethodHandle::class
        is Type.Named -> type.rawType
        is Type.Var -> Any::class
        is Type.Generic -> Any::class
    }.java