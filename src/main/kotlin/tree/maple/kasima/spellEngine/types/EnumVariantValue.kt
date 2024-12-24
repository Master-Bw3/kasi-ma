package tree.maple.kasima.spellEngine.types

import net.minecraft.util.Identifier
import kotlin.reflect.KClass


data class VariantType(val fields: List<Type<*>>)

abstract class EnumType : Type<EnumVariantValue>() {
    abstract val id: Identifier
    abstract val variants: Map<Identifier, VariantType>

    fun of(variantID: Identifier, values: List<Value>): EnumVariantValue {
        if (variantID in variants && values.zip(variants[variantID]!!.fields)
                .all { (value, type) -> value.type == type }
        ) {
            return EnumVariantValue(variantID, values, this)
        } else {
            throw IllegalArgumentException()
        }
    }

    override fun of(value: Any): EnumVariantValue {
        if (value is EnumVariantValue && value.type == this) {
            return value
        } else {
            throw IllegalArgumentException()
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
