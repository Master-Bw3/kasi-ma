package tree.maple.kasima.spellEngine.compiler

import java.lang.invoke.MethodHandle
import kotlin.reflect.KClass

interface BaseType

sealed class Type {
    data class Named(val name: String, val rawType: KClass<*>, val genericArgs: List<Type>) : Type(), BaseType {
        override fun toString(): String = "$name<${genericArgs.joinToString(", ")}>"
    }

    data class Function(val signature: FunctionCons) : Type() {
        companion object {
            operator fun invoke(signature: List<Type>) = Function(listToFunctionCons(signature))
        }
    }

    //type var that has not been inferred yet
    data class Var(val index: Int) : Type(), BaseType {
        override fun toString(): String = "$$index"
    }

    //generic type that stands in place of any type
    data class Generic(val id: UInt) : Type(), BaseType {
        override fun toString(): String = "T$id"
    }
}

sealed class FunctionCons {
    data class Leaf<T>(val type: T) : FunctionCons() where T : Type, T : BaseType {
        override fun toString(): String = type.toString()
    }

    data class Arrow(val left: FunctionCons, val right: FunctionCons) : FunctionCons() {
        override fun toString(): String {
            val x = when (this.left) {
                is Arrow -> "(${this.left})"
                is Leaf<*> -> this.left.toString()
            }
            return "$x -> $right"
        }
    }

    val length: Int
        get() = when (this) {
            is Leaf<*> -> 1
            is Arrow -> 1 + this.right.length
        }

    fun removeAt(index: Int): FunctionCons = when (this) {
        is Leaf<*> -> this
        is Arrow -> if (index == 0) {
            this.right
        } else {
            Arrow(this.left, this.right.removeAt(index.dec()))
        }
    }

    operator fun get(index: Int): Type? = when (this) {
        is Leaf<*> -> if (index == 0) this.type else null
        is Arrow -> if (index == 0) {
            this.first()
        } else {
            this.right[index.dec()]
        }
    }

    fun first(): Type = when (this) {
        is Leaf<*> -> this.type
        is Arrow -> when (this.left) {
            is Leaf<*> -> this.left.type
            is Arrow -> Type.Function(this.left)
        }
    }

    fun last(): Type = when (this) {
        is Leaf<*> -> this.type
        is Arrow -> when (this.right) {
            is Leaf<*> -> this.right.type
            is Arrow -> this.right.last()
        }
    }

    fun map(transform: (Type) -> Type): FunctionCons =
        when (this) {
            is Arrow -> Arrow(this.left.map(transform), this.right.map(transform))
            is Leaf<*> -> when (val result = transform(this.type)) {
                is BaseType -> Leaf(result)

                is Type.Function -> result.signature

                else -> throw Error("unreachable")
            }
        }

    fun toList(): List<Type> = when (this) {
        is Leaf<*> -> listOf(this.type)

        is Arrow -> when (this.left) {
            is Leaf<*> -> listOf(this.left.type) + this.right.toList()
            is Arrow -> listOf(Type.Function(this.left)) + this.right.toList()
        }
    }
}

fun listToFunctionCons(signature: List<Type>): FunctionCons {
    var functionCons: FunctionCons =
        when (val last = signature.last()) {
            is BaseType -> FunctionCons.Leaf(last)

            is Type.Function -> last.signature

            else -> throw Error("unreachable")
        }

    for (i in signature.size - 2 downTo 0) {
        val type = signature[i]

        functionCons = when (type) {
            is BaseType -> FunctionCons.Arrow(FunctionCons.Leaf(type), functionCons)

            is Type.Function -> FunctionCons.Arrow(type.signature, functionCons)

            else -> throw Error("unreachable")
        }
    }

    return functionCons
}

fun main() {
    println(listToFunctionCons(listOf(BuiltInType.number, BuiltInType.list(BuiltInType.number))))
}


object BuiltInType {
    val number = Type.Named("number", Double::class, listOf())
    fun list(a: Type) = Type.Named("list", List::class, listOf(a))
    fun tuple(genericArgs: List<Type>) = Type.Named("tuple", List::class, genericArgs)
}

fun getRawType(type: Type): Class<*> =
    when (type) {
        is Type.Function -> MethodHandle::class
        is Type.Named -> type.rawType
        is Type.Var -> Any::class
        is Type.Generic -> Any::class
    }.java