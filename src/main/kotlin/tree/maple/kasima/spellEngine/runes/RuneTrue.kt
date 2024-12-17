package tree.maple.kasima.spellEngine.runes

import tree.maple.kasima.spellEngine.types.BooleanValue
import tree.maple.kasima.spellEngine.types.Type

object RuneTrue : Rune(3u) {
    override val arguments: List<Type<*>>
        get() = listOf()
    override val returnType: Type<*>
        get() = BooleanValue.TYPE

    @JvmStatic
    fun apply() = true
}