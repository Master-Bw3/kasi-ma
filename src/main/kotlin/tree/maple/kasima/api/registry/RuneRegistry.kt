package tree.maple.kasima.api.registry

import com.mojang.serialization.Lifecycle
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.block.Block
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.util.Identifier
import tree.maple.kasima.KasiMa
import tree.maple.kasima.spellEngine.Rune
import java.util.function.Supplier


private val REGISTRY_KEY: RegistryKey<Registry<RuneRegistryEntry>> =
    RegistryKey.ofRegistry(KasiMa.id("rune"))

private val REGISTRY: Registry<RuneRegistryEntry> = FabricRegistryBuilder.from(
    SimpleRegistry(
        REGISTRY_KEY,
        Lifecycle.stable()
    )
).buildAndRegister()


object RuneRegistry : Registry<RuneRegistryEntry> by REGISTRY {
    fun register(rune: Rune, block: Supplier<Block>, material: Supplier<Block>, id: Identifier): Rune {
        return Registry.register(
            REGISTRY,
            id,
            RuneRegistryEntry(rune, block, material)
        ).rune
    }

    fun initialize() {}
}

data class RuneRegistryEntry(val rune: Rune, val block: Supplier<Block>, val material: Supplier<Block>)