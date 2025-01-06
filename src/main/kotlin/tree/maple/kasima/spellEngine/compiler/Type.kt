package tree.maple.kasima.spellEngine.compiler

import java.lang.invoke.MethodHandle
import kotlin.reflect.KClass

sealed class Type {
    data class Named(val name: String, val rawType: KClass<*>, val genericArgs: List<Type>) : Type() {
        override fun toString(): String = "$name<${genericArgs.joinToString(", ")}>"
    }

    //type var that has not been inferred yet
    data class Var(val index: Int) : Type() {
        override fun toString(): String = "$$index"
    }

    //generic type that stands in place of any type
    data class Generic(val id: UInt) : Type() {
        override fun toString(): String = "T$id"
    }

    data class Function(val left: Type, val right: Type) : Type() {
        companion object {
            operator fun invoke(signature: List<Type>) = signatureToFunction(signature)
        }

        override fun toString(): String {
            val x = when (this.left) {
                is Function -> "(${this.left})"
                else -> this.left.toString()
            }
            return "$x -> $right"
        }

        val length: Int
            get() = 1 + when (this.right) {
                is Function -> this.right.length
                else -> 1
            }

        fun removeAt(index: Int): Type =
            if (index == 0) {
                this.right
            } else if (this.right is Function) {
                Function(this.left, this.right.removeAt(index.dec()))
            } else if (index == 1) {
                this.left
            } else {
                throw IndexOutOfBoundsException()
            }


        operator fun get(index: Int): Type? =
            if (index == 0) {
                this.left
            } else if (this.right is Function) {
                this.right[index.dec()]
            } else if (index == 1) {
                this.right
            } else {
                null
            }

        fun first(): Type = this.left

        fun last(): Type =
            when (this.right) {
                is Function -> this.right.last()
                else -> this.right
            }

        fun map(transform: (Type) -> Type): Function =
            when (this.right) {
                is Function -> Function(transform(this.left), this.right.map(transform))
                else -> Function(transform(this.left), transform(this.right))
            }

        fun toList(): List<Type> =
            when (this.right) {
                is Function -> listOf(this.left) + this.right.toList()
                else -> listOf(this.left, this.right)
            }

        fun toList(maxSize: Int): List<Type> {
            require(maxSize > 0)
            return toListTruncatedAt(maxSize - 1)
        }

        private fun toListTruncatedAt(index: Int): List<Type> =
            if (index == 0) {
                listOf(this)
            } else when (this.right) {
                is Function -> listOf(this.left) + this.right.toListTruncatedAt(index - 1)

                else -> listOf(this.left, this.right)
            }


    }
}


fun signatureToFunction(signature: List<Type>): Type.Function {
    require(signature.size > 1)

    var function = signature.last()

    signature.dropLast(1).foldRight(function) { type, acc ->
        Type.Function(type, acc)
    }

    for (i in signature.size - 2 downTo 0) {
        val type = signature[i]

        function = Type.Function(type, function)
    }

    return function as Type.Function
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