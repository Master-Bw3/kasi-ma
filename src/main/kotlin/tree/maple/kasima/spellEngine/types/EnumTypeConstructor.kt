package tree.maple.kasima.spellEngine.types

import net.minecraft.util.Identifier
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import kotlin.reflect.KClass


data class VariantType(val fields: List<TypeConstructor<*>>)

abstract class EnumTypeConstructor : TypeConstructor<EnumVariantValue>() {
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
            override val signature: List<TypeConstructor<*>> = variant.fields.plus(this@EnumTypeConstructor)

            override val handle: MethodHandle by lazy {
                val lookup = MethodHandles.lookup()

                val methodType = MethodType.methodType(signature.last().rawType.java, Array<Any>::class.java)
                var handle = lookup.findVirtual(this::class.java, "apply", methodType)

                handle = MethodHandles.insertArguments(handle, 0, this)

                handle
                    .asCollector(methodType.lastParameterType(), signature.size)
                    .asType(MethodType.methodType(signature.last().rawType.java, signature.map { it.rawType.java }))
            }

            fun apply(args: Array<Any>): EnumVariantValue {
                return this@EnumTypeConstructor.construct(
                    variantID,
                    args.zip(signature).map { (raw, type) -> type.construct(raw) })
            }

        }
    }

    override val rawType: KClass<*> = EnumVariantValue::class

    override val string: String
        get() = "Enum($id)"

    override fun equals(other: Any?): Boolean {
        return other is EnumTypeConstructor && other.id == this.id && other.variants == this.variants
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}

data class EnumVariantValue(val variantID: Identifier, val values: List<Value>, override val type: EnumTypeConstructor) : Value() {

    override fun toString(): String {
        return "$variantID(${values.joinToString(", ")})"
    }
}
