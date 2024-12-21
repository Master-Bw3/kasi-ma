package tree.maple.kasima.spellEngine.runes

import tree.maple.kasima.spellEngine.types.NumberValue
import tree.maple.kasima.spellEngine.types.Type
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

object RuneOne : Rune() {
    override val arguments: List<Type<*>> = listOf()

    override val returnType: Type<*>
        get() = NumberValue.TYPE

    @JvmStatic
    fun apply(): Double = 1.0
}