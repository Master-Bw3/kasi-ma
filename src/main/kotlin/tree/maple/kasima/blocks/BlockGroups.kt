package tree.maple.kasima.blocks

import net.minecraft.block.Block
import net.minecraft.registry.Registries

object BlockGroups {

    val translucent
        get() = Registries.BLOCK.filter { it is Translucent }

    val axeMineable
        get() = Registries.BLOCK.filter { it is AxeMineable }
}