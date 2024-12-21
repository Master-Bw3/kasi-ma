package tree.maple.kasima.blocks.blockEntities

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import tree.maple.kasima.KasiMa


object KasimaBlockEntities {
    fun <T : BlockEntityType<*>?> register(path: String, blockEntityType: T): T {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, KasiMa.id(path), blockEntityType)
    }

    fun initialize() {
    }
}