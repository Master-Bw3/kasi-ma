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


    fun <T: Block> register(from: Identifier, to: T): T {

        Registry.register(REGISTRY, from, to)

        return to
    }

    fun initialize() {}

}