package tree.maple.kasima.spellEngine.types

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import kotlin.reflect.KClass

abstract class SpellFunctionTypeConstructor : TypeConstructor<SpellFunction>() {
    abstract val signature: List<TypeConstructor<*>>

}

abstract class SpellFunction : Value() {

    abstract val signature: List<TypeConstructor<*>>

    open val handle: MethodHandle by lazy {
        val lookup = MethodHandles.lookup()
        val returnType = signature.last()
        val methodType = MethodType.methodType(returnType.rawType.java, signature.dropLast(1).map { it.rawType.java })
        lookup.findStatic(this::class.java, "apply", methodType)
    }


    override val type: TypeConstructor<*> = object : SpellFunctionTypeConstructor() {
        override val signature: List<TypeConstructor<*>>
            get() = this@SpellFunction.signature

        override fun construct(value: Any): SpellFunction {
            if (value is SpellFunction && value == this) {
                return value
            } else {
                throw IllegalArgumentException()
            }
        }

        override val rawType: KClass<*>
            get() = SpellFunction::class

        override val string: String
            get() = signature.joinToString(" -> ") { it.string }

        override fun equals(other: Any?): Boolean {
            return other is SpellFunctionTypeConstructor && signature == other.signature
        }
    }

    override fun toString(): String {
        return this.type.string
    }
}