package tree.maple.kasima.spellEngine.types

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import kotlin.reflect.KClass

abstract class SpellFunctionType : Type<SpellFunction>() {
    abstract val arguments: List<Type<*>>

    abstract val returnType: Type<*>
}

abstract class SpellFunction : Value() {

    abstract val arguments: List<Type<*>>

    abstract val returnType: Type<*>

    open val handle: MethodHandle by lazy {
        val lookup = MethodHandles.lookup()
        val methodType = MethodType.methodType(returnType.rawType.java, arguments.map { it.rawType.java })
        lookup.findStatic(this::class.java, "apply", methodType)
    }


    override val type: Type<*> = object : SpellFunctionType() {
        override val arguments: List<Type<*>>
            get() = this@SpellFunction.arguments

        override val returnType: Type<*>
            get() = this@SpellFunction.returnType

        override fun of(value: Any): SpellFunction {
            if (value is SpellFunction && value == this) {
                return value
            } else {
                throw IllegalArgumentException()
            }
        }

        override val rawType: KClass<*>
            get() = SpellFunction::class

        override val string: String
            get() = "(${arguments.joinToString(", ")}) -> $returnType"

        override fun equals(other: Any?): Boolean {
            return other is SpellFunction && arguments == other.arguments && returnType == other.returnType
        }
    }

    override fun toString(): String {
        return "(${arguments.joinToString(", ") { it.string }}) -> ${returnType.string}"
    }
}