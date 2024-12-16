package tree.maple.kasima.spellEngine.runes

import com.google.common.base.Suppliers
import tree.maple.kasima.spellEngine.types.NumberValue
import tree.maple.kasima.spellEngine.types.Type
import tree.maple.kasima.spellEngine.types.Value
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import kotlin.reflect.KClass

object RuneAdd : Rune() {
    override val arguments: List<Type<*>> = listOf(NumberValue.TYPE, NumberValue.TYPE)

    override val returnType: Type<*>
        get() = NumberValue.TYPE

    @JvmStatic
    fun apply(a: Double, b: Double): Double = a + b
}