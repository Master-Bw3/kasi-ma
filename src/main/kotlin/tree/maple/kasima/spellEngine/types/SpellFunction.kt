package tree.maple.kasima.spellEngine.types

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import kotlin.reflect.KClass

abstract class SpellFunction : Value() {

    abstract val arguments : List<Type<*>>

    abstract val returnType : Type<*>

    open val handle: MethodHandle by lazy {
        val lookup = MethodHandles.lookup()
        val methodType = MethodType.methodType(returnType.rawType.java, arguments.map { it.rawType.java })
        lookup.findStatic(this::class.java, "apply", methodType)
    }

    val TYPE = object : Type<SpellFunction>() {
        override fun of(value: Any): SpellFunction  {
            if (value == this) {
                return value as SpellFunction
            } else {
                throw IllegalArgumentException()
            }
        }

        override val rawType: KClass<*>
            get() = SpellFunction::class

        override fun equals(other: Any?): Boolean {
            return other is SpellFunction &&
                    arguments == other.arguments &&
                    returnType == other.returnType
        }
    }

    override fun toString(): String {
        return "Function(${this})"
    }
}