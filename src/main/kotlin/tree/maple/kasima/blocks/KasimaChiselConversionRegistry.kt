package tree.maple.kasima.blocks

import com.mojang.serialization.Lifecycle
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa

object KasimaChiselConversionRegistry {
    val REGISTRY_KEY: RegistryKey<Registry<Block>> =
        RegistryKey.ofRegistry(KasiMa.id("chisel_conversion"))

    val REGISTRY: Registry<Block> = FabricRegistryBuilder.from(
        SimpleRegistry(
            REGISTRY_KEY,
            Lifecycle.stable()
        )
    ).buildAndRegister()


    fun register(from: Block, to: Block): Block {
        val fromID = Registries.BLOCK.getId(from)

        Registry.register(REGISTRY, fromID, to)

        return from
    }

    fun initialize() {}

}