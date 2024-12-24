package tree.maple.kasima.spellEngine.types

import net.minecraft.util.Identifier
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import kotlin.reflect.KClass


data class VariantType(val fields: List<Type<*>>)

abstract class EnumType : Type<EnumVariantValue>() {
    abstract val id: Identifier
    abstract val variants: Map<Identifier, VariantType>

    fun construct(variantID: Identifier, values: List<Value>): EnumVariantValue {
        if (variantID in variants && values.zip(variants[variantID]!!.fields)
                .all { (value, type) -> value.type == type }
        ) {
            return EnumVariantValue(variantID, values, this)
        } else {
            throw IllegalArgumentException()
        }
    }

    override fun construct(value: Any): EnumVariantValue {
        if (value is EnumVariantValue && value.type == this) {
            return value
        } else {
            throw IllegalArgumentException()
        }
    }

    fun generateConstructor(variantID: Identifier): SpellFunction {
        if (variantID !in variants) throw IllegalArgumentException()
        val variant = variants[variantID]!!
        return object : SpellFunction() {
            override val arguments: List<Type<*>> = variant.fields

            override val returnType: Type<*> = this@EnumType

            override val handle: MethodHandle by lazy {
                val lookup = MethodHandles.lookup()

                val methodType = MethodType.methodType(returnType.rawType.java, Array<Any>::class.java)
                var handle = lookup.findVirtual(this::class.java, "apply", methodType)

                handle = MethodHandles.insertArguments(handle, 0, this)

                handle
                    .asCollector(methodType.lastParameterType(), arguments.size)
                    .asType(MethodType.methodType(returnType.rawType.java, arguments.map { it.rawType.java }))
            }

            fun apply(args: Array<Any>): EnumVariantValue {
                return this@EnumType.construct(
                    variantID,
                    args.zip(arguments).map { (raw, type) -> type.construct(raw) })
            }

        }
    }

    override val rawType: KClass<*> = EnumVariantValue::class

    override val string: String
        get() = "Enum($id)"

    override fun equals(other: Any?): Boolean {
        return other is EnumType && other.id == this.id && other.variants == this.variants
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}

data class EnumVariantValue(val variantID: Identifier, val values: List<Value>, override val type: EnumType) : Value() {

    override fun toString(): String {
        return "$variantID(${values.joinToString(", ")})"
    }
}
