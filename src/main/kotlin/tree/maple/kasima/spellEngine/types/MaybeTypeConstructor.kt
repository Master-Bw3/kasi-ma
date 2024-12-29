package tree.maple.kasima.spellEngine.types

import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa


class MaybeTypeConstructor : EnumTypeConstructor() {
    val MAYBE: Identifier = KasiMa.id("maybe")
    val SOME: Identifier = KasiMa.id("some")
    val NONE: Identifier = KasiMa.id("none")

    override val id: Identifier = MAYBE

    override val generics: List<Type> = listOf(TypeVariable("a"))

    override val variants: Map<Identifier, VariantType> =
        mapOf(
            SOME to VariantType(listOf(NumberTypeConstructor)),
            NONE to VariantType(listOf())
        )


}