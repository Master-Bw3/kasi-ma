package tree.maple.kasima.blocks.blockEntities

import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.blocks.KasimaBlockRegistry


object KasimaBlockEntityTypeRegistry {
    fun <T : BlockEntityType<*>?> register(path: String, blockEntityType: T): T {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, KasiMa.id(path), blockEntityType)
    }

    val PALE_RUNE_CORE: BlockEntityType<RuneCoreBlockEntity> = register(
        "pale_rune_core",
        FabricBlockEntityTypeBuilder.create(::RuneCoreBlockEntity, KasimaBlockRegistry.PALE_RUNE_CORE).build()
    )

    fun initialize() {
    }
}