package tree.maple.kasima.spellEngine.compiler

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
    val NUMBER = Type.Named("Number", Double::class, listOf())
    val LIST = Type.Named("Number", Double::class, listOf(Type.Generic(1u)))
}