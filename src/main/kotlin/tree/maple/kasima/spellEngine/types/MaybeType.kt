package tree.maple.kasima.spellEngine.types

import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa


object MaybeType : EnumType() {
    val MAYBE: Identifier = KasiMa.id("maybe")
    val SOME: Identifier = KasiMa.id("some")
    val NONE: Identifier = KasiMa.id("none")

    override val id: Identifier = MAYBE

    override val variants: Map<Identifier, VariantType> =
        mapOf(
            SOME to VariantType(listOf(NumberValue.TYPE)),
            NONE to VariantType(listOf())
        )


}